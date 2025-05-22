package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.application.exceptions.*;
import com.github.zigcat.greenhub.payment_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.payment_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.payment_provider.domain.PaymentSession;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ScopeType;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.SubscriptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

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

    public Flux<AppSubscription> listAll(ServerHttpRequest request){
        return Flux.just(permissions.extractAuthData(request))
                .flatMap(auth -> {
                    if(!auth.getScopes().contains(ScopeType.PAYMENT_VIEW.getScope())){
                        return Mono.error(new ForbiddenAppException("User hasn't access for this action"));
                    }
                    return subscriptions.retrieveByUserId(auth.getId());
                });
    }

    public Mono<AppSubscription> retrieveActive(ServerHttpRequest request){
        return Mono.just(permissions.extractAuthData(request))
                .flatMap(auth -> {
                    if(!auth.getScopes().contains(ScopeType.PAYMENT_VIEW.getScope())){
                        return Mono.error(new ForbiddenAppException("User hasn't access for this action"));
                    }
                    return subscriptions.getActiveOrPendingSubscription(auth.getId())
                            .switchIfEmpty(Mono.error(new NotFoundAppException("Active subscription not found")));
                });
    }

    public Mono<PaymentSession> createSession(ServerHttpRequest request, Long planId) {
        log.info("Creating payment session for planId: {}", planId);
        return Mono.just(permissions.extractAuthData(request))
                .flatMap(auth -> {
                    log.info("Auth data: {}", auth);
                    if (auth.isAdmin()) {
                        return Mono.error(new ForbiddenAppException("Admins cannot create subscriptions"));
                    }
                    return subscriptions.hasActiveSubscriptions(auth.getId())
                        .flatMap(hasActive -> {
                            if(hasActive) {
                                return Mono.error(new ConflictAppException("User already has an active or pending subscription"));
                            }
                            return plans.retrieve(planId)
                                .switchIfEmpty(Mono.error(new NotFoundAppException(
                                        "Plan with ID " + planId + " not found")))
                                .flatMap(plan -> {
                                    String providerPlanId = plan.getStripePriceId();
                                    if (providerPlanId == null) {
                                        return Mono.error(new IllegalStateException(
                                                "Provider plan ID is missing for " + provider.getName()));
                                    }
                                    return provider.createSubscription(
                                            auth.getUsername(),
                                            auth.getId(),
                                            providerPlanId
                                        )
                                        .switchIfEmpty(Mono.error(new IllegalStateException(
                                                "Failed to create subscription session")))
                                        .flatMap(paymentSession -> {
                                            AppSubscription subscription = new AppSubscription(
                                                auth.getId(),
                                                plan.getId(),
                                                provider.getName(),
                                                paymentSession.getProviderCustomerId(),
                                                paymentSession.getProviderSessionId(),
                                                SubscriptionStatus.PENDING
                                            );
                                            return subscriptions.save(subscription)
                                                .doOnSuccess(newSub ->
                                                        log.info("Subscription created: {}", newSub))
                                                .map(newSub -> paymentSession);
                                        });
                                });
                        });
                })
                .onErrorMap(e -> {
                    if (e instanceof ForbiddenAppException
                            || e instanceof ConflictAppException
                            || e instanceof NotFoundAppException) {
                        return e;
                    }
                    log.error("Error creating payment session: {}", e.getMessage(), e);
                    return new ServerErrorAppException("Failed to create payment session");
                })
                .subscribeOn(Schedulers.boundedElastic());
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
                        .then(Mono.just(activeSub));
            });
    }

    public Mono<String> handleStripeWebhook(ServerHttpRequest request, String payload){
        log.info("Received event from Stripe Webhook");
        return provider.handleWebhook(request, payload)
            .flatMap(webhookSub -> {
                ArrayList<SubscriptionStatus> target = new ArrayList<>(List.of(SubscriptionStatus.PENDING));
                if(webhookSub.getStatus() != SubscriptionStatus.ACTIVE) target.add(SubscriptionStatus.ACTIVE);
                return subscriptions.retrieveByCustomerId(webhookSub.getProviderCustomerId(), target)
                        .flatMap(subscription -> {
                            log.info("Original data: {}", webhookSub);
                            log.info("Setting data: {}", subscription);
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
                        });
                }
            );
    }
}
