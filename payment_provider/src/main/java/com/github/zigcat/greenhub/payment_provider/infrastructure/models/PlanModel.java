package com.github.zigcat.greenhub.payment_provider.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("subscription_plans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanModel {
    @Id
    @Column("plan_id")
    private Long id;
    @Column("name")
    private String name;
    @Column("price")
    private Double price;
    @Column("currency")
    private String currency;
    @Column("paypal_plan_id")
    private String paypalPlanId;
    @Column("stripe_price_id")
    private String stripePriceId;

    public PlanModel(String name, Double price, String currency, String paypalPlanId, String stripePriceId) {
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.paypalPlanId = paypalPlanId;
        this.stripePriceId = stripePriceId;
    }
}
