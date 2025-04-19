package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.article_provider.domain.Interaction;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.InteractionRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;
import com.github.zigcat.greenhub.article_provider.utils.InteractionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class InteractionService {
    private final InteractionRepository interactions;
    private final PermissionService permissions;

    public InteractionService(
            InteractionRepository interactions,
            PermissionService permissions
    ) {
        this.interactions = interactions;
        this.permissions = permissions;
    }

    public Mono<Interaction> retrieve(Long articleId){
        Interaction interaction = new Interaction(articleId);
        return interactions.findByArticleId(articleId)
                .collectList()
                .map(models -> {
                    int totalLikes = 0;
                    int totalViews = 0;
                    int totalRating = 0;
                    int ratingCount = 0;
                    for (InteractionModel model : models) {
                        if (Boolean.TRUE.equals(model.getLike())) totalLikes++;
                        if (model.getViews() != null) totalViews += model.getViews();
                        if (model.getRating() != null) {
                            totalRating += model.getRating();
                            ratingCount++;
                        }
                    }
                    interaction.setLikes(totalLikes);
                    interaction.setViews(totalViews);
                    interaction.setRating(ratingCount > 0 ? (double) totalRating / ratingCount : 0.0);
                    return interaction;
                });
    }

    public Mono<Interaction> retrieveByUserAndArticle(Long userId,
                                                      Long articleId,
                                                      ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(auth.isAdmin() || auth.getId().equals(userId)){
            return interactions.findByUserAndArticle(userId, articleId)
                    .map(InteractionUtils::toEntity);
        }
        return Mono.error(new ForbiddenAppException("Access Denied"));
    }

    public Mono<Interaction> upsert(Interaction interaction, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        return interactions.upsert(
                auth.getId(),
                interaction.getArticleId(),
                interaction.getLikes(),
                interaction.getViews(),
                interaction.getRating()
        ).map(InteractionUtils::toEntity);
    }
}
