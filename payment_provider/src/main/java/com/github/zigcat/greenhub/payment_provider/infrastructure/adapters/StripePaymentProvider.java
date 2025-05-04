package com.github.zigcat.greenhub.payment_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.payment_provider.domain.PaymentSession;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.SourceInfrastructureException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
public class StripePaymentProvider implements PaymentProvider {
    public StripePaymentProvider(@Value("${stripe.key.secret}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    private Mono<String> getOrCreateCustomer(String userEmail) {
        return Mono.fromCallable(() -> {
            Customer existingCustomer = findCustomerByEmail(userEmail);
            if (existingCustomer != null) {
                return existingCustomer.getId();
            }
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(userEmail)
                    .build();
            Customer newCustomer = Customer.create(params);
            return newCustomer.getId();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Customer findCustomerByEmail(String email) throws StripeException {
        List<Customer> customers = Customer.search(
                CustomerSearchParams.builder().setQuery("email:'"+email+"'").build()
        ).getData();
        return customers.isEmpty() ? null : customers.get(0);
    }

    @Override
    public ProviderName getName() {
        return ProviderName.STRIPE;
    }

    @Override
    public Mono<PaymentSession> createSubscription(String email, String planId) {
        return getOrCreateCustomer(email)
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
                            return new PaymentSession(session.getId(), session.getCustomer(), session.getUrl());
                        })
                ).subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Mono.error(new SourceInfrastructureException("Stripe services are unavailable")));
    }

    @Override
    public Mono<Void> cancelSubscription(String subscriptionId) {
        return Mono.fromCallable(() -> {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            subscription.cancel();
            return Mono.empty();
        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(e ->
                Mono.error(new SourceInfrastructureException(
                "Stripe services are unavailable"
                )))
        .then();
    }
}
