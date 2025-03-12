package com.github.zigcat.greenhub.payment_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PlanRepository;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.r2dbc.ReactivePlanRepository;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.SourceInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.models.PlanModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class R2dbcPlanRepository implements PlanRepository {
    private final ReactivePlanRepository repository;

    public R2dbcPlanRepository(ReactivePlanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<PlanModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    throw new SourceInfrastructureException("Payment service unavailable");
                });
    }

    @Override
    public Mono<PlanModel> findById(Long id) {
        return repository.findById(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found Plan with this ID");
                    }
                    throw new SourceInfrastructureException("Payment service unavailable");
                });
    }

    @Override
    public Mono<PlanModel> save(PlanModel model) {
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
