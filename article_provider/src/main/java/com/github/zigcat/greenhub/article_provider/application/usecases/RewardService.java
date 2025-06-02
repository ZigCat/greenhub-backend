package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.article_provider.domain.AuthorReward;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.*;
import com.github.zigcat.greenhub.article_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.article_provider.infrastructure.mappers.RewardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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

    public RewardService(AuthorRewardRepository repository, UserRepository users, SubscriptionRepository subscriptions, InteractionRepository interactions, ArticleRepository articles) {
        this.repository = repository;
        this.users = users;
        this.subscriptions = subscriptions;
        this.interactions = interactions;
        this.articles = articles;
    }

    public Flux<Void> calculateMonthlyReward(){
        log.info("Starting calculating monthly reward for each author...");
        return subscriptions.listAllActive()
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
                                            .flatMap(model -> users
                                                    .retrieve(model.getCreator())
                                                    .filter(u -> u.getRole().equalsIgnoreCase(Role.AUTHOR.toString()))
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
                                    .map(rew -> {
                                        log.info("Calculated: {}", rew);
                                        return RewardMapper.toModel(rew);
                                    })
//                                    .collectList()
//                                    .flatMapMany(rewards -> repository.saveAll(rewards))
                                    .then();
                        })
                )
                .doOnError(e -> log.error("Reward calculation failed: {}", e.getMessage()));
    }
}
