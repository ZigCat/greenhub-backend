package com.github.zigcat.greenhub.payment_provider.infrastructure.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.payment_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.payment_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.payment_provider.domain.PaymentSession;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.UserProvider;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.SubscriptionStatus;
import com.github.zigcat.greenhub.payment_provider.exceptions.CoreException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.BadRequestInfrastructureException;
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
            SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(true)
                .build();
            Subscription.retrieve(subscriptionId).update(params);
            return null;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(e -> new SourceInfrastructureException("Stripe service error"))
        .then();
    }

    @Override
    public Mono<Void> cancelSubscriptionImmediately(String subscriptionId) {
        return Mono.fromCallable(() -> {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            String latestInvoiceId = subscription.getLatestInvoice();
            if (latestInvoiceId == null) {
                throw new BadRequestInfrastructureException("No invoice found for refund");
            }
            Invoice invoice = Invoice.retrieve(latestInvoiceId);
            if (invoice.getCharge() == null) {
                throw new BadRequestInfrastructureException("No charge found for invoice");
            }
            subscription.cancel(Map.of("invoice_now", true, "prorate", true));
            Refund.create(Map.of("charge", invoice.getCharge()));

            return null;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(e -> {
            if(e instanceof CoreException) return e;
            return new SourceInfrastructureException("Stripe services are unavailable");
        })
        .then();
    }

    public Mono<AppSubscription> handleWebhook(ServerHttpRequest request, String payload){
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
                        return handleInvoiceEvent(event, SubscriptionStatus.ACTIVE);
                    }
                    case "invoice.payment_failed" -> {
                        return handleInvoiceEvent(event, SubscriptionStatus.PAYMENT_FAILED);
                    }
                    case "customer.subscription.deleted" -> {
                        return handleSubscriptionDelete(event);
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

    private Mono<AppSubscription> handleSessionCompleted(Event event) {
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
                    SubscriptionStatus.ACTIVE,
                    Instant.ofEpochSecond(sub.getCurrentPeriodStart()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime(),
                    Instant.ofEpochSecond(sub.getCurrentPeriodEnd()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime());
            log.info("Data extracted from event: {}", subscription);
            return subscription;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<AppSubscription> handleInvoiceEvent(Event event, SubscriptionStatus status){
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
                    status,
                    Instant.ofEpochSecond(sub.getCurrentPeriodStart()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime(),
                    Instant.ofEpochSecond(sub.getCurrentPeriodEnd()).atZone(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime());
            log.info("Data extracted from event: {}", subscription);
            return subscription;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<AppSubscription> handleSubscriptionDelete(Event event){
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
            return subscription;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
