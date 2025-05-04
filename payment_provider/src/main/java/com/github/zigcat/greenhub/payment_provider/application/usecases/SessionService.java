package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.payment_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.payment_provider.domain.Subscription;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.SubscriptionStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private final SubscriptionService subscriptions;
    private final PlanService plans;
    private final PermissionService permissions;
    private final Map<ProviderName, PaymentProvider> providers;


    @Value("${stripe.key.webhook}")
    private String STRIPE_WEBHOOK_SECRET;

    public SessionService(SubscriptionService subscriptions, PlanService plans, PermissionService permissions, List<PaymentProvider> providers) {
        this.subscriptions = subscriptions;
        this.plans = plans;
        this.permissions = permissions;
        this.providers = providers.stream()
                .collect(Collectors.toMap(PaymentProvider::getName, Function.identity()));
    }

    public Mono<String> createSession(ServerHttpRequest request, String type, Long planId){
        PaymentProvider provider;
        try{
            ProviderName providerName = ProviderName.valueOf(type);
            provider = providers.get(providerName);
        } catch (IllegalArgumentException e){
            return Mono.error(new BadRequestAppException("Illegal provider name"));
        }
        AuthorizationData auth = permissions.extractAuthData(request);
        return plans.retrieve(planId)
            .flatMap(plan ->
                provider.createSubscription(
                    auth.getUsername(),
                    provider.getName() == ProviderName.STRIPE
                        ? plan.getStripePriceId()
                        : plan.getPaypalPlanId()
                ).map(paymentSession -> {
                    Subscription subscription = new Subscription(
                        auth.getId(),
                        plan.getId(),
                        provider.getName(),
                        paymentSession.getProviderCustomerId(),
                        paymentSession.getProviderSessionId(),
                        SubscriptionStatus.PENDING
                    );
                    subscriptions.save(subscription);
                    return paymentSession.getUrl();
                })
            );
    }

    public Mono<String> handleStripeWebhook(ServerHttpRequest request, String payload){
        return Mono.fromCallable(() -> {
            String sigHeader = request.getHeaders().getFirst("Stripe-Signature");
            Event event;
            try {
                event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);
            } catch (SignatureVerificationException e){
                return Mono.error(new BadRequestAppException("Invalid signature"));
            }
            switch(event.getType()){
                case "checkout.session.completed" -> {}
                case "invoice.paid" -> {}
                case "invoice.payment_failed" -> {}
                case "customer.subscription.deleted" -> {}
                default -> {}
            }
        });
    }
}
