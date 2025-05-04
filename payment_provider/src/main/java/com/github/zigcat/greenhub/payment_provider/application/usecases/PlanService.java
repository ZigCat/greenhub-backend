package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.domain.Plan;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PlanRepository;
import com.github.zigcat.greenhub.payment_provider.infrastructure.mappers.PlanMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlanService {
    private final PlanRepository repository;

    public PlanService(PlanRepository repository) {
        this.repository = repository;
    }

    public Flux<Plan> list(){
        return repository.findAll().map(PlanMapper::toEntity);
    }

    public Mono<Plan> retrieve(Long id){
        return repository.findById(id).map(PlanMapper::toEntity);
    }

    public Mono<Plan> save(Plan plan){
        return repository.save(PlanMapper.toModel(plan)).map(PlanMapper::toEntity);
    }

    public Mono<Void> delete(Long id){
        return repository.delete(id);
    }
}
