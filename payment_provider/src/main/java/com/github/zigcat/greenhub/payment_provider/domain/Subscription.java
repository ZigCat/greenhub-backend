package com.github.zigcat.greenhub.payment_provider.domain;

import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    private Long id;
    private Long userId;
    private Long planId;
    private ProviderName provider;
    private String providerSubscriptionId;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
