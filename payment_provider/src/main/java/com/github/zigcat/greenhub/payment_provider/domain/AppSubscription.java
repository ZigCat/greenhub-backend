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
public class AppSubscription {
    private Long id;
    private Long userId;
    private Long planId;
    private ProviderName provider;
    private String providerSubscriptionId;
    private String providerCustomerId;
    private String providerSessionId;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public AppSubscription(Long userId, Long planId, ProviderName provider, String providerCustomerId, String providerSessionId, SubscriptionStatus status) {
        this.userId = userId;
        this.planId = planId;
        this.provider = provider;
        this.providerCustomerId = providerCustomerId;
        this.providerSessionId = providerSessionId;
        this.status = status;
    }

    public AppSubscription(ProviderName provider, String providerSubscriptionId, String providerCustomerId, String providerSessionId, SubscriptionStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        this.provider = provider;
        this.providerSubscriptionId = providerSubscriptionId;
        this.providerCustomerId = providerCustomerId;
        this.providerSessionId = providerSessionId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
