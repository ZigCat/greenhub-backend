package com.github.zigcat.greenhub.payment_provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSession {
    private String providerSessionId;
    private String providerCustomerId;
    private LocalDateTime createdAt;
    private String url;

    public PaymentSession(String providerSessionId, String providerCustomerId, String url) {
        this.providerSessionId = providerSessionId;
        this.providerCustomerId = providerCustomerId;
        this.createdAt = LocalDateTime.now();
        this.url = url;
    }
}
