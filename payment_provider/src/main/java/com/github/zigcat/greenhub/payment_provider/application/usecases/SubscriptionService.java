package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.payment_provider.application.exceptions.ServerErrorAppException;
import com.github.zigcat.greenhub.payment_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.SubscriptionRepository;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.SubscriptionStatus;
import com.github.zigcat.greenhub.payment_provider.infrastructure.mappers.SubscriptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository repository;

    public SubscriptionService(SubscriptionRepository repository) {
        this.repository = repository;
    }
    public Flux<AppSubscription> listAllActive(){
        return repository.findAllActive()
                .map(SubscriptionMapper::toEntity);
    }

    public Flux<AppSubscription> list(){
        return repository.findAll().map(SubscriptionMapper::toEntity);
    }

    public Mono<AppSubscription> retrieve(Long id){
        return repository.findById(id).map(SubscriptionMapper::toEntity);
    }

    public Flux<AppSubscription> retrieveByUserId(Long userId){
        return repository.findByUserId(userId).map(SubscriptionMapper::toEntity);
    }

    public Mono<AppSubscription> retrieveByCustomerId(String id, List<SubscriptionStatus> target){
        return repository.findAllByCustomerId(id)
                .filter(sub -> {
                    for(SubscriptionStatus status: target){
                        if(sub.getStatus().equals(status)) return true;
                    }
                    return false;
                })
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundAppException("No subscriptions with target statuses")))
                .map(SubscriptionMapper::toEntity);
    }

    public Mono<AppSubscription> save(AppSubscription subscription){
        return repository.save(SubscriptionMapper.toModel(subscription))
                .map(SubscriptionMapper::toEntity)
                .doOnNext(saved -> log.info("New subscription saved: {}", saved))
                .doOnError(e -> log.error("Error saving new subscription: {}", e.getMessage()));
    }

    public Mono<AppSubscription> getActiveOrPendingSubscription(Long userId) {
        return repository.findByUserId(userId)
                .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE
                        || sub.getStatus() == SubscriptionStatus.PENDING
                        || sub.getStatus() == SubscriptionStatus.PAYMENT_FAILED
                        || sub.getStatus() == SubscriptionStatus.CANCEL_AWAITING)
                .map(SubscriptionMapper::toEntity)
                .next()
                .doOnNext(sub -> log.debug("User {} has active or pending subscription: {}", userId, sub))
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("User {} has no active or pending subscriptions", userId);
                    return Mono.empty();
                }))
                .onErrorResume(e -> {
                    log.error("Error checking subscriptions for user {}: {}", userId, e.getMessage(), e);
                    return Mono.error(new ServerErrorAppException("Failed to check subscriptions"));
                });
    }


    public Mono<Boolean> hasActiveSubscriptions(Long userId) {
        return repository.findByUserId(userId)
                .any(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE
                        || sub.getStatus() == SubscriptionStatus.PENDING
                        || sub.getStatus() == SubscriptionStatus.PAYMENT_FAILED
                        || sub.getStatus() == SubscriptionStatus.CANCEL_AWAITING)
                .doOnSuccess(hasActive -> log.debug("User {} has active subscriptions: {}", userId, hasActive))
                .onErrorResume(e -> {
                    log.error("Error checking active subscriptions for user {}: {}", userId, e.getMessage(), e);
                    return Mono.error(new ServerErrorAppException("Failed to check active subscriptions"));
                });
    }

    public Mono<Void> delete(Long id){
        return repository.delete(id);
    }

    @Scheduled(fixedRate = 3600000)
    public void schedulePendingExpiration() {
        log.info("Executing scheduled task for pending subscriptions expiration");
        expirePendingSubscriptions(Duration.ofHours(24))
                .subscribe(
                        null,
                        error -> log.error("Error in scheduled task: {}", error.getMessage())
                );
    }

    @Scheduled(fixedRate = 3600000)
    public void scheduleCanceling() {
        log.info("Executing scheduled task for canceling subscriptions");
        cancelAwaitingSubscriptions()
                .subscribe(
                        null,
                        error -> log.error("Error in scheduled task: {}", error.getMessage())
                );
    }

    private Mono<Void> expirePendingSubscriptions(Duration pendingLifetime) {
        LocalDateTime cutoff = LocalDateTime.now().minus(pendingLifetime);
        return repository.expireOldPendingSubscriptions(cutoff)
                .then();
    }

    private Mono<Void> cancelAwaitingSubscriptions(){
        LocalDateTime present = LocalDateTime.now();
        return repository.cancelAwaitingSubscriptions(present)
                .then();
    }
}
