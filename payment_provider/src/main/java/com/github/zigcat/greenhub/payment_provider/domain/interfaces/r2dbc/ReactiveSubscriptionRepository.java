package com.github.zigcat.greenhub.payment_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.payment_provider.infrastructure.models.SubscriptionModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ReactiveSubscriptionRepository extends ReactiveCrudRepository<SubscriptionModel, Long> {
    Flux<SubscriptionModel> findAllByUserId(Long userId);
    Flux<SubscriptionModel> findAllByProviderCustomerId(String providerCustomerId);
    @Query("UPDATE user_subscriptions SET status = 'EXPIRED' " +
            "WHERE status = 'PENDING' AND created_at < :cutoff")
    Mono<Integer> expireOldPendingSubscriptions(@Param("cutoff") LocalDateTime cutoff);
}
