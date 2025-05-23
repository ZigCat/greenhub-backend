package com.github.zigcat.greenhub.payment_provider.domain;

import com.github.zigcat.greenhub.payment_provider.domain.schemas.StripeEventName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripeEvent {
    private StripeEventName eventName;
    private AppSubscription appSubscription;
}
