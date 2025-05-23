package com.github.zigcat.greenhub.payment_provider.infrastructure.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.payment_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.payment_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.payment_provider.domain.PaymentSession;
import com.github.zigcat.greenhub.payment_provider.domain.StripeEvent;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.UserProvider;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.StripeEventName;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.SubscriptionStatus;
import com.github.zigcat.greenhub.payment_provider.exceptions.ClientErrorException;
import com.github.zigcat.greenhub.payment_provider.exceptions.CoreException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.ServerErrorInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.SourceInfrastructureException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StripePaymentProvider implements PaymentProvider {
    @Value("${stripe.key.webhook}")
    private String STRIPE_WEBHOOK_SECRET;
    private final UserProvider userProvider;

    public StripePaymentProvider(@Value("${stripe.key.secret}") String apiKey, UserProvider userProvider) {
        this.userProvider = userProvider;
        Stripe.apiKey = apiKey;
    }

    private Mono<String> getOrCreateCustomer(String userEmail, Long userId) {
        log.info("Checking customer: {}", userEmail);
        return findCustomer(userId)
                .flatMap(existingCustomer -> Mono.just(existingCustomer.getId()))
                .switchIfEmpty(createCustomer(userEmail, userId)
                        .flatMap(newCustomer -> userProvider.promote(userId)
                                    .onErrorResume(e -> {
                                        if(e instanceof ClientErrorException ce && ce.getCode() == 409){
                                            return Mono.just(new InfrastructureDTO.ScopeDTO(null, userId, "payment.view"));
                                        }
                                        throw new SourceInfrastructureException("Failed to create Stripe customer");
                                    })
                                    .thenReturn(newCustomer.getId())
                        ))
                .onErrorMap(e -> {
                    if (e instanceof CoreException) return e;
                    return new SourceInfrastructureException("Failed to create Stripe customer");
                });
    }

    private Mono<Customer> findCustomer(Long id) {
        return Mono.fromCallable(() -> {
            List<Customer> customers = Customer.search(
                    CustomerSearchParams.builder()
                            .setQuery("metadata['gi_id']:'" + id + "'")
                            .build()
            ).getData();
            return customers.isEmpty() ? null : customers.get(0);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Customer> createCustomer(String email, Long id) {
        return Mono.fromCallable(() -> {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(email)
                    .putMetadata("gi_id", id.toString())
                    .putMetadata("gi_email", email)
                    .build();
            Customer newCustomer = Customer.create(params);
            log.info("New customer created: {}", newCustomer);
            return newCustomer;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public ProviderName getName() {
        return ProviderName.STRIPE;
    }

    @Override
    public Mono<PaymentSession> createSubscription(String email, Long id, String planId) {
        log.info("Creating Stripe Checkout session");
        return getOrCreateCustomer(email, id)
                .flatMap(customerId ->
                        Mono.fromCallable(() -> {
                            SessionCreateParams params = SessionCreateParams.builder()
                                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                                    .setCustomer(customerId)
                                    .setSuccessUrl("http://green-insight.space")
                                    .setCancelUrl("http://green-insight.space/user/1")
                                    .addLineItem(
                                            SessionCreateParams.LineItem.builder()
                                                    .setQuantity(1L)
                                                    .setPrice(planId)
                                                    .build()
                                    )
                                    .build();
                            Session session = Session.create(params);
                            PaymentSession paymentSession = new PaymentSession(session.getId(), session.getCustomer(), session.getUrl());
                            log.info("Successfully created Checkout session {}", paymentSession);
                            return paymentSession;
                        })
                ).subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error occurred while trying to create session: {}", e.getMessage());
                    return new SourceInfrastructureException("Stripe services are unavailable");
                });
    }

    @Override
    public Mono<Void> cancelSubscription(String subscriptionId) {
        return Mono.fromCallable(() -> {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            if (!"active".equals(subscription.getStatus())) {
                throw new BadRequestInfrastructureException("Subscription is not active and cannot be cancelled");
            }
            if (!Boolean.FALSE.equals(subscription.getCancelAtPeriodEnd())) {
                throw new BadRequestInfrastructureException("Subscription is already cancelled");
            }
            SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(true)
                .build();
            subscription.update(params);
            return null;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(e -> {
            if (e instanceof CoreException) return e;
            return new SourceInfrastructureException("Stripe services are unavailable");
        })
        .then();
    }

    @Override
    public Mono<Void> resumeSubscription(String subscriptionId) {
        return Mono.fromCallable(() -> {
                    Subscription subscription = Subscription.retrieve(subscriptionId);
                    if (!"active".equals(subscription.getStatus())) {
                        throw new BadRequestInfrastructureException("Subscription is not active and cannot be resumed");
                    }
                    if (!Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd())) {
                        throw new BadRequestInfrastructureException("Subscription is not scheduled for cancellation");
                    }
                    SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                            .setCancelAtPeriodEnd(false)
                            .build();
                    subscription.update(params);
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    if (e instanceof CoreException) return e;
                    return new SourceInfrastructureException("Stripe services are unavailable");
                })
                .then();
    }

    @Override
    public Mono<Void> refundSubscription(String subscriptionId) {
        return Mono.fromCallable(() -> Subscription.retrieve(subscriptionId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(subscription -> {
                    String latestInvoiceId = subscription.getLatestInvoice();
                    if (latestInvoiceId == null) {
                        return Mono.error(new BadRequestInfrastructureException("No invoice found for refund"));
                    }
                    return Mono.just(latestInvoiceId);
                })
                .flatMap(latestInvoiceId -> Mono.fromCallable(() -> Invoice.retrieve(latestInvoiceId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .flatMap(invoice -> {
                    if (invoice.getCharge() == null) {
                        return Mono.error(new BadRequestInfrastructureException("No charge found for invoice"));
                    }
                    Map<String, Object> cancelParams = Map.of(
                            "invoice_now", true,
                            "prorate", true
                    );
                    return Mono.fromCallable(() -> {
                                Subscription subscription = Subscription.retrieve(subscriptionId);
                                subscription.cancel(cancelParams);
                                return invoice.getCharge();
                            })
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .flatMap(chargeId -> Mono.fromCallable(() -> {
                    Refund.create(Map.of("charge", chargeId));
                    return true;
                }).subscribeOn(Schedulers.boundedElastic()))
                .then()
                .onErrorMap(e -> {
                    if (e instanceof CoreException) return e;
                    return new SourceInfrastructureException("Stripe services are unavailable");
                });
    }

    public Mono<StripeEvent> handleWebhook(ServerHttpRequest request, String payload){
        String sigHeader = request.getHeaders().getFirst("Stripe-Signature");
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);
        } catch (SignatureVerificationException e){
            return Mono.error(new BadRequestAppException("Invalid signature"));
        }
        log.info("Event processed, type = {}", event.getType());
        return Mono.defer(() -> {
            try{
                switch(event.getType()){
                    case "checkout.session.completed" -> {
                        return handleSessionCompleted(event);
                    }
                    case "invoice.payment_succeeded" -> {
                        return handleInvoiceEvent(event, StripeEventName.PAYMENT_SUCCEEDED);
                    }
                    case "invoice.payment_failed" -> {
                        return handleInvoiceEvent(event, StripeEventName.PAYMENT_FAILED);
                    }
                    case "customer.subscription.deleted" -> {
                        return handleSubscriptionDelete(event);
                    }
                    case "customer.subscription.updated" -> {
                        return handleSubscriptionUpdated(event);
                    }
                    default -> {
                        return Mono.error(new BadRequestInfrastructureException("Unhandled event"));
                    }
                }
            } catch(Exception e){
                log.error("Error while processing event {}", e.getMessage());
                if(e instanceof CoreException ce) return Mono.error(ce);
                return Mono.error(new ServerErrorInfrastructureException("Unknown server error"));
            }
        });
    }

    private Mono<StripeEvent> handleSessionCompleted(Event event) {
        return Mono.fromCallable(() -> {
            String jsonData = event.getDataObjectDeserializer().getRawJson();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(jsonData);
            String sessionId = node.get("id").asText();
            String subscriptionId = node.get("subscription").asText();
            log.info("Deserialized : "+sessionId+", "+subscriptionId);
            Subscription sub = Subscription.retrieve(subscriptionId);
            AppSubscription subscription = new AppSubscription(
                    ProviderName.STRIPE,
                    sub.getId(),
                    sub.getCustomer(),
                    sessionId,
                    SubscriptionStatus.PENDING,
                    Instant.ofEpochSecond(sub.getCurrentPeriodStart()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime(),
                    Instant.ofEpochSecond(sub.getCurrentPeriodEnd()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime());
            log.info("Data extracted from event: {}", subscription);
            return new StripeEvent(StripeEventName.SESSION_COMPLETED, subscription);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<StripeEvent> handleInvoiceEvent(Event event, StripeEventName eventName){
        return Mono.fromCallable(() -> {
            String jsonData = event.getDataObjectDeserializer().getRawJson();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(jsonData);
            String invoiceId = node.get("id").asText();
            String subscriptionId = node.get("lines").get("data").get(0).get("subscription").asText();
            Subscription sub = Subscription.retrieve(subscriptionId);
            AppSubscription subscription = new AppSubscription(
                    ProviderName.STRIPE,
                    sub.getId(),
                    sub.getCustomer(),
                    invoiceId,
                    eventName.equals(StripeEventName.PAYMENT_SUCCEEDED)
                            ? SubscriptionStatus.ACTIVE
                            : SubscriptionStatus.PAYMENT_FAILED,
                    Instant.ofEpochSecond(sub.getCurrentPeriodStart()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime(),
                    Instant.ofEpochSecond(sub.getCurrentPeriodEnd()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime());
            log.info("Data extracted from event: {}", subscription);
            return new StripeEvent(eventName, subscription);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<StripeEvent> handleSubscriptionUpdated(Event event){
        return Mono.fromCallable(() -> {
            String jsonData = event.getDataObjectDeserializer().getRawJson();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(jsonData);
            String subscriptionId = node.get("id").asText();
            Subscription sub = Subscription.retrieve(subscriptionId);
            Map<String, Object> previousAttributes = event.getData().getPreviousAttributes();
            if(previousAttributes != null){
                if(previousAttributes.containsKey("cancel_at_period_end")){
                    SubscriptionStatus status;
                    if(Boolean.TRUE.equals(previousAttributes.get("cancel_at_period_end"))){
                        log.info("Subscription cancellation was undone, id = {}", sub.getId());
                        status = SubscriptionStatus.ACTIVE;
                    } else {
                        log.info("Subscription was cancelled manually, id = {}", sub.getId());
                        status = SubscriptionStatus.CANCEL_AWAITING;
                    }
                    AppSubscription subscription = new AppSubscription(
                            ProviderName.STRIPE,
                            sub.getId(),
                            sub.getCustomer(),
                            null,
                            status,
                            Instant.ofEpochSecond(sub.getCurrentPeriodStart()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime(),
                            Instant.ofEpochSecond(sub.getCurrentPeriodEnd()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime()
                    );
                    log.info("Data extracted from event: {}", subscription);
                    return new StripeEvent(StripeEventName.SUBSCRIPTION_UPDATED, subscription);
                } else {
                    throw new NotFoundInfrastructureException("Nothing to update with this params");
                }
            } else {
                log.warn("Deserialization error");
                throw new ServerErrorInfrastructureException("Stripe object deserialization error");
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<StripeEvent> handleSubscriptionDelete(Event event){
        return Mono.fromCallable(() -> {
            String jsonData = event.getDataObjectDeserializer().getRawJson();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(jsonData);
            String subscriptionId = node.get("id").asText();
            String customerId = node.get("customer").asText();
            long startDate = node.get("current_period_start").asLong();
            long endDate = node.get("current_period_end").asLong();
            AppSubscription subscription = new AppSubscription(
                    ProviderName.STRIPE,
                    subscriptionId,
                    customerId,
                    null,
                    SubscriptionStatus.CANCELED,
                    Instant.ofEpochSecond(startDate).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime(),
                    Instant.ofEpochSecond(endDate).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime()
            );
            log.info("Data extracted from event: {}", subscription);
            return new StripeEvent(StripeEventName.SUBSCRIPTION_DELETED, subscription);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
