package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.payment_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.payment_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.payment_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.payment_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.payment_provider.domain.PaymentSession;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.SubscriptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SessionService {
    private final SubscriptionService subscriptions;
    private final PlanService plans;
    private final PermissionService permissions;
    private final PaymentProvider provider;

    public SessionService(SubscriptionService subscriptions, PlanService plans, PermissionService permissions, PaymentProvider provider) {
        this.subscriptions = subscriptions;
        this.plans = plans;
        this.permissions = permissions;
        this.provider = provider;
    }

    public Mono<PaymentSession> createSession(ServerHttpRequest request, Long planId){
        log.info("Creating payment session");
        AuthorizationData auth = permissions.extractAuthData(request);
        log.info("Auth data: {}", auth);
        if(auth.isAdmin()) return Mono.error(new ForbiddenAppException("You're admin, dummy :)"));
        return plans.retrieve(planId)
            .flatMap(plan ->
                provider.createSubscription(
                    auth.getUsername(),
                    auth.getId(),
                    provider.getName() == ProviderName.STRIPE
                        ? plan.getStripePriceId()
                        : plan.getPaypalPlanId()
                ).flatMap(paymentSession -> {
                    AppSubscription subscription = new AppSubscription(
                        auth.getId(),
                        plan.getId(),
                        provider.getName(),
                        paymentSession.getProviderCustomerId(),
                        paymentSession.getProviderSessionId(),
                        SubscriptionStatus.PENDING
                    );
                    return subscriptions.create(subscription)
                            .map(newSub -> {
                                log.info("Subscription created: {}", newSub);
                                return paymentSession;
                            });
                })
            );
    }

    public Mono<AppSubscription> cancelSubscription(ServerHttpRequest request){
        log.info("Canceling subscription manually");
        AuthorizationData auth = permissions.extractAuthData(request);
        return subscriptions.retrieveByUserId(auth.getId())
            .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE)
            .singleOrEmpty()
            .switchIfEmpty(Mono.error(new NotFoundAppException("No active subscription to cancel")))
            .flatMap(activeSub -> {
                log.info("Canceling subscription {}", activeSub);
                return provider.cancelSubscription(activeSub.getProviderSubscriptionId())
                        .then(Mono.defer(() -> {
                            activeSub.setStatus(SubscriptionStatus.CANCELED);
                            activeSub.setStartDate(null);
                            activeSub.setEndDate(LocalDateTime.now());
                            return subscriptions.save(activeSub);
                        }));
            });
    }

    public Mono<String> handleStripeWebhook(ServerHttpRequest request, String payload){
        log.info("Received event from Stripe WebHook");
        return provider.handleWebhook(request, payload)
            .flatMap(webhookSub -> subscriptions.retrieveBySessionId(webhookSub.getProviderSubscriptionId())
                .flatMap(subscription -> {
                    subscription.setStatus(webhookSub.getStatus());
                    subscription.setProviderSubscriptionId(webhookSub.getProviderSubscriptionId());
                    subscription.setStartDate(webhookSub.getStartDate());
                    subscription.setEndDate(webhookSub.getEndDate());
                    return subscriptions.save(subscription)
                            .map(saved -> {
                                log.info("Event processed with data {}", saved);
                                return "Subscription with ID "
                                        + saved.getProviderSubscriptionId()
                                        + " successfully processed, status = "
                                        + saved.getStatus();
                            });
                })
            );
    }
}
