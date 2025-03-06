package com.github.zigcat.greenhub.user_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.user_provider.infrastructure.models.ScopeModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveScopeRepository extends ReactiveCrudRepository<ScopeModel, Long> {

}
