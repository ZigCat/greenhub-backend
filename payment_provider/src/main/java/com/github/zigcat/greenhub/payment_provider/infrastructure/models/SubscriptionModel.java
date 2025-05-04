package com.github.zigcat.greenhub.payment_provider.infrastructure.models;

import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("user_subscriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionModel {
    @Id
    @Column("sub_id")
    private Long id;
    @Column("user_id")
    private Long userId;
    @Column("plan_id")
    private Long planId;
    @Column("provider")
    private ProviderName provider;
    @Column("provider_subscription_id")
    private String providerSubscriptionId;
    @Column("provider_customer_id")
    private String providerCustomerId;
    @Column("provider_session_id")
    private String providerSessionId;
    @Column("status")
    private SubscriptionStatus status;
    @Column("start_date")
    private LocalDateTime startDate;
    @Column("end_date")
    private LocalDateTime endDate;
}
