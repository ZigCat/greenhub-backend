package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubscriptionRepository {
    Mono<AppSubscription> retrieve(AuthorizationData auth);
    Flux<AppSubscription> listAllActive();
}
