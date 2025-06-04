package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.article_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.article_provider.domain.AuthorReward;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.*;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.article_provider.infrastructure.mappers.RewardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RewardService {
    private final AuthorRewardRepository repository;
    private final UserRepository users;
    private final SubscriptionRepository subscriptions;
    private final InteractionRepository interactions;
    private final ArticleRepository articles;
    private final PermissionService permissions;

    public RewardService(AuthorRewardRepository repository, UserRepository users, SubscriptionRepository subscriptions, InteractionRepository interactions, ArticleRepository articles, PermissionService permissions) {
        this.repository = repository;
        this.users = users;
        this.subscriptions = subscriptions;
        this.interactions = interactions;
        this.articles = articles;
        this.permissions = permissions;
    }

    public Flux<AuthorReward> retrieveByAuthorId(ServerHttpRequest request, Long authorId){
        return Mono.fromCallable(() -> permissions.extractAuthData(request))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(auth -> {
                    if(!auth.getId().equals(authorId) || !auth.isAdmin()) return Flux.error(new ForbiddenAppException("Access denied"));
                    return repository.findAllByAuthorId(authorId)
                            .map(RewardMapper::toEntity);
                });
    }

    public Flux<AuthorReward> calculateMonthlyReward(){
        log.info("Starting calculating monthly reward for each author...");
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return repository.findAllByCalculatedAtAfter(startOfMonth)
            .map(RewardMapper::toEntity)
            .map(AuthorReward::getAuthorId)
            .collectList()
            .flatMapMany(paidAuthors -> subscriptions.listAllActive()
                .onErrorResume(e -> {
                    log.error("An error occurred while trying to get subscriptions: {}", e.getMessage());
                    return Mono.error(e);
                })
                .collectList()
                .map(subs -> {
                    log.info("Collected subscriptions: {}", subs);
                    return subs.stream()
                                    .map(AppSubscription::getUserId)
                                    .distinct()
                                    .toList();
                        }
                ).flatMapMany(ids -> interactions
                        .findUserArticleInteractionLastMonth(ids)
                        .collectMultimap(Tuple2::getT1, Tuple2::getT2)
                        .flatMapMany(userToArticlesMap -> {
                            List<Mono<AuthorReward>> rewardMonos = new ArrayList<>();
                            for(var entry : userToArticlesMap.entrySet()){
                                List<Long> articleIds = new ArrayList<>(entry.getValue());
                                if (articleIds.isEmpty()) continue;

                                double rewardPerArticle = 2.0 / articleIds.size();
                                for(Long articleId : articleIds){
                                    Mono<AuthorReward> reward = articles
                                            .findById(articleId)
                                            .onErrorResume(e -> {
                                                log.warn("Skipping articleId {} due to error: {}", articleId, e.getMessage());
                                                return Mono.empty();
                                            })
                                            .filter(model -> model.getCreator() != null)
                                            .filter(model -> model.getPaidStatus().equals(PaidStatus.PAID))
                                            .flatMap(model -> users
                                                    .retrieve(model.getCreator())
                                                    .filter(u -> {
                                                        boolean isAuthor = u.getRole().equals(Role.AUTHOR.toString());
                                                        boolean notAlreadyRewarded = !paidAuthors.contains(u.getId());
                                                        log.info("Article: {}, user: {}, isAuthor = {}, alreadyRewarded = {}", model.getId(), u, isAuthor, !notAlreadyRewarded);
                                                        return isAuthor && notAlreadyRewarded;
                                                    })
                                                    .map(u -> new AuthorReward(u.getId(), rewardPerArticle, LocalDateTime.now())));

                                    rewardMonos.add(reward);
                                }
                            }
                            return Flux.merge(rewardMonos)
                                    .groupBy(AuthorReward::getAuthorId)
                                    .flatMap(group -> group
                                            .reduce((a, b) ->
                                                    new AuthorReward(a.getAuthorId(),
                                                            a.getReward() + b.getReward(),
                                                            a.getCalculatedAt())
                                            )
                                    )
                                    .doOnNext(rew -> log.info("Calculated: {}", rew))
                                    .map(RewardMapper::toModel)
                                    .collectList()
                                    .flatMapMany(repository::saveAll)
                                    .map(RewardMapper::toEntity);
                        })
                )
                .doOnError(e -> log.error("Reward calculation failed: {}", e.getMessage())));
    }

    @Scheduled(cron = "0 0 * * * *")
    public void scheduleRewarding(){
        calculateMonthlyReward().then();
    }
}
