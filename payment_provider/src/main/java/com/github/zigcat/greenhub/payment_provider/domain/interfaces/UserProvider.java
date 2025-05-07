package com.github.zigcat.greenhub.payment_provider.domain.interfaces;

import com.github.zigcat.greenhub.payment_provider.infrastructure.InfrastructureDTO;
import reactor.core.publisher.Mono;

public interface UserProvider {
    Mono<InfrastructureDTO.ScopeDTO> promote(Long userId);
}
