package com.github.zigcat.greenhub.payment_provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    private Long id;
    private String name;
    private Double price;
    private String currency;
    private String paypalPlanId;
    private String stripePriceId;
}
