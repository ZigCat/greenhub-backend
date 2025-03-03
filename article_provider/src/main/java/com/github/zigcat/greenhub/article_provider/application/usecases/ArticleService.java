package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleContentRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleRepository;
import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.utils.ArticleUtils;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ArticleService {
    private final ArticleRepository repository;
    private final ArticleContentRepository contentRepository;
    private final PermissionService permissions;

    public ArticleService(
            ArticleRepository repository,
            ArticleContentRepository contentRepository,
            PermissionService permissions) {
        this.repository = repository;
        this.contentRepository = contentRepository;
        this.permissions = permissions;
    }

    public Flux<Article> list(){
        return repository
                .findAll()
                .map(model -> ArticleUtils.toEntity(model, null));
    }

    public Mono<Article> retrieve(Long id){
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new NotFoundAppException("Article not found")))
                .flatMap(model -> contentRepository.findById(id)
                        .map(content -> ArticleUtils.toEntity(model, content)));
    }

    @Transactional
    public Mono<Article> create(DTO.ArticleCreateDTO dto, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(dto.isMissing()){
            return Mono.error(new BadRequestAppException("Missing data"));
        }
        if(!permissions.canPublish(auth)){
            return Mono.error(new ForbiddenAppException("User can't publish articles"));
        }
        PaidStatus paidStatus = permissions.canBePaid(auth);
        ArticleModel articleModel = new ArticleModel(dto.title(), LocalDateTime.now(), ArticleStatus.MODERATION, paidStatus, auth.getId(), dto.category());
        return repository.save(articleModel)
                .flatMap(model -> contentRepository
                        .save(new ArticleContentModel(model.getId(), dto.content()))
                        .map(content -> ArticleUtils.toEntity(model, content)))
                .onErrorResume(ex -> repository.delete(articleModel.getId()).then(Mono.error(ex)));
    }

    @Transactional
    public Mono<Article> update(DTO.ArticleCreateDTO dto, Long articleId, ServerHttpRequest request) {
        AuthorizationData auth = permissions.extractAuthData(request);
        return retrieve(articleId)
                .flatMap(entity -> {
                    if (dto.isMissing()) {
                        return Mono.error(new BadRequestAppException("Missing data"));
                    }
                    if (!permissions.canEdit(auth, entity)) {
                        return Mono.error(new ForbiddenAppException("User can't edit article(s)"));
                    }
                    entity.setTitle(dto.title());
                    entity.setContent(dto.content());
                    entity.setCategory(dto.category());
                    Mono<ArticleModel> savedArticle = repository.save(ArticleUtils.toModel(entity));
                    Mono<ArticleContentModel> updatedContent = contentRepository.findById(entity.getId())
                            .switchIfEmpty(Mono.error(new NotFoundAppException("Article content not found")))
                            .flatMap(content -> {
                                content.setContent(entity.getContent());
                                return contentRepository.save(content);
                            });
                    return Mono.zip(savedArticle, updatedContent)
                            .map(tuple -> ArticleUtils.toEntity(tuple.getT1(), tuple.getT2()));
                });
    }

    @Transactional
    public Mono<Void> delete(Long articleId, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        return retrieve(articleId)
                .flatMap(entity -> {
                    if(!permissions.canDelete(auth, entity)){
                        return Mono.error(new ForbiddenAppException("User can't delete article(s)"));
                    }
                    return Mono.when(
                            repository.delete(entity.getId()),
                            contentRepository.delete(entity.getId())
                    );
                });
    }
}
