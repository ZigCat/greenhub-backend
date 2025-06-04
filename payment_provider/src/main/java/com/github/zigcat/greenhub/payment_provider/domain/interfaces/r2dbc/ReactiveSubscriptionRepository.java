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
    @Query("SELECT * FROM user_subscriptions " +
            "WHERE status IN ('ACTIVE', 'CANCEL_AWAITING') " +
            "AND start_date::date <= NOW() AND end_date::date >= NOW()")
    Flux<SubscriptionModel> findAllActive();
    @Query("UPDATE user_subscriptions SET status = 'EXPIRED' " +
            "WHERE status IN ('PENDING', 'PAYMENT_FAILED') AND created_at < :cutoff")
    Mono<Integer> expireOldPendingSubscriptions(@Param("cutoff") LocalDateTime cutoff);

    @Query("UPDATE user_subscriptions SET status = 'CANCELED' " +
            "WHERE status = 'CANCEL_AWAITING' AND end_date < :present")
    Mono<Integer> cancelAwaitingSubscriptions(@Param("present") LocalDateTime present);
}
