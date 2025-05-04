package com.github.zigcat.greenhub.payment_provider.presentation.controllers;

import com.github.zigcat.greenhub.payment_provider.application.usecases.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webhook/stripe")
public class StripeWebhookController {
    private final SessionService service;

    public StripeWebhookController(SessionService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> handleWebhook(@RequestBody String payload,
                                              ServerHttpRequest request) {
        return service.handleStripeWebhook(request, payload)
                .map(ResponseEntity::ok);
    }
}
