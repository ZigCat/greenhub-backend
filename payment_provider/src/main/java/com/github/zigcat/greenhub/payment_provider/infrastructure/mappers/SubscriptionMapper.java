package com.github.zigcat.greenhub.payment_provider.infrastructure.mappers;

import com.github.zigcat.greenhub.payment_provider.domain.Subscription;
import com.github.zigcat.greenhub.payment_provider.infrastructure.models.SubscriptionModel;

public class SubscriptionMapper {
    public static Subscription toEntity(SubscriptionModel model){
        return new Subscription(
                model.getId(),
                model.getUserId(),
                model.getPlanId(),
                model.getProvider(),
                model.getProviderSubscriptionId(),
                model.getProviderCustomerId(),
                model.getProviderSessionId(),
                model.getStatus(),
                model.getStartDate(),
                model.getEndDate()
        );
    }

    public static SubscriptionModel toModel(Subscription entity){
        return new SubscriptionModel(
                entity.getId(),
                entity.getUserId(),
                entity.getPlanId(),
                entity.getProvider(),
                entity.getProviderSubscriptionId(),
                entity.getProviderCustomerId(),
                entity.getProviderSessionId(),
                entity.getStatus(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}
