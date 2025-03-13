package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.payment_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.payment_provider.domain.Subscription;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.domain.interfaces.SubscriptionRepository;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class SubscriptionService {
    private final List<PaymentProvider> providers;
    private final SubscriptionRepository repository;
    private final PermissionService permissions;

    public SubscriptionService(List<PaymentProvider> providers, SubscriptionRepository repository, PermissionService permissions) {
        this.providers = providers;
        this.repository = repository;
        this.permissions = permissions;
    }

    public Flux<Subscription> list(ServerHttpRequest request, Long userId){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!permissions.canViewPayments(auth)) return Flux.error(new ForbiddenAppException("Access denied"));
        if(auth.isAdmin()){
            if(userId != null){
                return repository.findByUserId(userId)
                        .
            }

        } else {

        }
    }
}
