package com.github.zigcat.greenhub.payment_provider.infrastructure.mappers;

import com.github.zigcat.greenhub.payment_provider.domain.Plan;
import com.github.zigcat.greenhub.payment_provider.infrastructure.models.PlanModel;

public class PlanMapper {
    public static Plan toEntity(PlanModel model){
        return new Plan(
                model.getId(),
                model.getName(),
                model.getPrice(),
                model.getCurrency(),
                model.getPaypalPlanId(),
                model.getStripePriceId()
        );
    }

    public static PlanModel toModel(Plan entity){
        return new PlanModel(
                entity.getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getCurrency(),
                entity.getPaypalPlanId(),
                entity.getStripePriceId()
        );
    }
}
