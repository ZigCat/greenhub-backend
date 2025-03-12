package com.github.zigcat.greenhub.payment_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.payment_provider.infrastructure.models.SubscriptionModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReactiveSubscriptionRepository extends ReactiveCrudRepository<SubscriptionModel, Long> {
    Mono<SubscriptionModel> findByUserId(Long userId);
}
