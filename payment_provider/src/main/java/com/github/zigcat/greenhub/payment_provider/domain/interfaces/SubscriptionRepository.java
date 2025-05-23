package com.github.zigcat.greenhub.payment_provider.domain.interfaces;

import com.github.zigcat.greenhub.payment_provider.infrastructure.models.SubscriptionModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface SubscriptionRepository {
    Flux<SubscriptionModel> findAll();
    Mono<SubscriptionModel> findById(Long id);
    Flux<SubscriptionModel> findByUserId(Long userId);
    Flux<SubscriptionModel> findAllByCustomerId(String id);
    Mono<Integer> expireOldPendingSubscriptions(LocalDateTime cutoff);
    Mono<Integer> cancelAwaitingSubscriptions(LocalDateTime cutoff);
    Mono<SubscriptionModel> save(SubscriptionModel model);
    Mono<Void> delete(Long id);
}
