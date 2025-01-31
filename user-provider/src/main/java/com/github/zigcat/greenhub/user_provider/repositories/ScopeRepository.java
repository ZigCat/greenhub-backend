package com.github.zigcat.greenhub.user_provider.repositories;

import com.github.zigcat.greenhub.user_provider.entities.Scope;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ScopeRepository extends ReactiveCrudRepository<Scope, Long> {

}
