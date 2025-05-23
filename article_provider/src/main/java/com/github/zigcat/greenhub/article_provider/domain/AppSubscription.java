package com.github.zigcat.greenhub.article_provider.domain;

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
    private String provider;
    private String providerSubscriptionId;
    private String providerCustomerId;
    private String providerSessionId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
