package com.github.zigcat.greenhub.payment_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.payment_provider.domain.interfaces.SubscriptionRepository;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.r2dbc.ReactiveSubscriptionRepository;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.SourceInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.models.SubscriptionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class R2dbcSubscriptionRepository implements SubscriptionRepository {
    private final ReactiveSubscriptionRepository repository;

    public R2dbcSubscriptionRepository(ReactiveSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<SubscriptionModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    throw new SourceInfrastructureException("Payment service unavailable");
                });
    }

    @Override
    public Mono<SubscriptionModel> findById(Long id) {
        return repository.findById(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found Subscription with this ID");
                    }
                    throw new SourceInfrastructureException("Payment service unavailable");
                });
    }

    @Override
    public Flux<SubscriptionModel> findByUserId(Long userId) {
        return repository.findAllByUserId(userId)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found Subscription");
                    }
                    throw new SourceInfrastructureException("Payment service unavailable");
                });
    }

    @Override
    public Flux<SubscriptionModel> findAllByCustomerId(String id) {
        return repository.findAllByProviderCustomerId(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found Subscription");
                    }
                    throw new SourceInfrastructureException("Payment service unavailable");
                });
    }

    @Override
    public Mono<Integer> expireOldPendingSubscriptions(LocalDateTime cutoff) {
        return repository.expireOldPendingSubscriptions(cutoff);
    }

    @Override
    public Mono<SubscriptionModel> save(SubscriptionModel model) {
        return repository.save(model)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    } else if(e instanceof IllegalArgumentException){
                        throw new BadRequestInfrastructureException("Constraints rules wasn't satisfied");
                    }
                    throw new SourceInfrastructureException("Payment service unavailable");
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id)
                .onErrorMap(e -> {
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new SourceInfrastructureException("Payment service unavailable");
                });
    }
}
