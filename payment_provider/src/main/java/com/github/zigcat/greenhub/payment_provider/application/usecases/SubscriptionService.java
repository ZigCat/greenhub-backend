package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.domain.Subscription;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.SubscriptionRepository;
import com.github.zigcat.greenhub.payment_provider.infrastructure.mappers.SubscriptionMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SubscriptionService {
    private final SubscriptionRepository repository;

    public SubscriptionService(SubscriptionRepository repository) {
        this.repository = repository;
    }

    public Flux<Subscription> list(){
        return repository.findAll().map(SubscriptionMapper::toEntity);
    }

    public Mono<Subscription> retrieve(Long id){
        return repository.findById(id).map(SubscriptionMapper::toEntity);
    }

    public Mono<Subscription> retrieveBySessionId(String sessionId){
        return repository.findBySessionId(sessionId).map(SubscriptionMapper::toEntity);
    }

    public Mono<Subscription> retrieveBySubscriptionId(String subscriptionId){
        return repository.findByProviderSubId(subscriptionId).map(SubscriptionMapper::toEntity);
    }

    public Mono<Subscription> retrieveByCustomerId(String customerId){
        return repository.findByCustomerId(customerId).map(SubscriptionMapper::toEntity);
    }

    public Mono<Subscription> save(Subscription subscription){
        return repository.save(SubscriptionMapper.toModel(subscription))
                .map(SubscriptionMapper::toEntity);
    }

    public Mono<Void> delete(Long id){
        return repository.delete(id);
    }
}
