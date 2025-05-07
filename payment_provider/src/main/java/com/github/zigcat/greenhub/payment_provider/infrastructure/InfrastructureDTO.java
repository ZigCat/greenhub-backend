package com.github.zigcat.greenhub.payment_provider.infrastructure;

public class InfrastructureDTO {
    public record ScopeDTO(
        Long id,
        Long userId,
        String scope
    ){}
}
