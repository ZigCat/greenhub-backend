package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.domain.Subscription;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.SubscriptionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class SubscriptionService {
    private final List<PaymentProvider> providers;
    private final SubscriptionRepository repository;

    public SubscriptionService(List<PaymentProvider> providers, SubscriptionRepository repository) {
        this.providers = providers;
        this.repository = repository;
    }
}
