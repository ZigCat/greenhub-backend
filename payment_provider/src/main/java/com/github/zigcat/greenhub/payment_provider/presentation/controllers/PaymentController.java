package com.github.zigcat.greenhub.payment_provider.presentation.controllers;

import com.github.zigcat.greenhub.payment_provider.application.usecases.SessionService;
import com.github.zigcat.greenhub.payment_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.payment_provider.domain.PaymentSession;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/protected/subscription")
public class PaymentController {
    private final SessionService service;

    public PaymentController(SessionService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<ResponseEntity<AppSubscription>> retrieve(ServerHttpRequest request){
        return service.retrieveActive(request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/active")
    public Flux<AppSubscription> listAllActive(ServerHttpRequest request){
        return service.listAllActive(request);
    }

    @GetMapping("/list")
    public Flux<AppSubscription> listAll(ServerHttpRequest request){
        return service.listAll(request);
    }

    @PostMapping("/create/{plan}")
    public Mono<ResponseEntity<PaymentSession>> createSession(
            @PathVariable("plan") Long plan,
            ServerHttpRequest request){
        return service.createSession(request, plan)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/cancel")
    public Mono<ResponseEntity<?>> cancelSubscription(
            ServerHttpRequest request
    ){
        return service.cancelSubscription(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/refund")
    public Mono<ResponseEntity<?>> cancelAndRefund(
            ServerHttpRequest request
    ){
        return service.cancelAndRefundSubscription(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/resume")
    public Mono<ResponseEntity<?>> resumeSubscription(
            ServerHttpRequest request
    ){
        return service.resumeSubscription(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
