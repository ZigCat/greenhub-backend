package com.github.zigcat.greenhub.payment_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.payment_provider.infrastructure.models.PlanModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactivePlanRepository extends ReactiveCrudRepository<PlanModel, Long> {
}
