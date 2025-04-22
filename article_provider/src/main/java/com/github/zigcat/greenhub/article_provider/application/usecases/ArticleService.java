package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.article_provider.domain.*;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleContentRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.UserRepository;
import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import com.github.zigcat.greenhub.article_provider.utils.ArticleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ArticleService {
    private final ArticleRepository repository;
    private final ArticleContentRepository contentRepository;
    private final UserRepository userRepository;
    private final PermissionService permissions;
    private final CategoryService categoryService;
    private final InteractionService interactionService;
    private final RecommendationService recommendationService;

    public ArticleService(
            ArticleRepository repository,
            ArticleContentRepository contentRepository,
            UserRepository userRepository,
            PermissionService permissions,
            CategoryService categoryService,
            InteractionService interactionService,
            RecommendationService recommendationService
    ) {
        this.repository = repository;
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
        this.permissions = permissions;
        this.categoryService = categoryService;
        this.interactionService = interactionService;
        this.recommendationService = recommendationService;
    }

    public Flux<Article> list(
            ServerHttpRequest request,
            String articleStatus,
            Long creatorId
    ){
        AuthorizationData auth = permissions.extractAuthData(request);
        ArticleStatus status = ArticleStatus.GRANTED;
        if(permissions.isAdmin(auth)){
            try{
               status = ArticleStatus.valueOf(articleStatus);
            } catch (IllegalArgumentException e){
                throw new BadRequestAppException("Wrong param");
            }
        }
        log.info(status.getValue());
        return repository
                .findAllByStatus(status.getValue())
                .filter(model -> {
                    if(creatorId == null) return true;
                    return model.getCreator().equals(creatorId);
                })
                .switchIfEmpty(Flux.error(new NotFoundAppException("Articles not found")))
                .flatMap(model -> {
                    Mono<AppUser> creator = userRepository.retrieve(model.getCreator())
                            .switchIfEmpty(Mono.just(new AppUser()));
                    Mono<Category> category = categoryService.retrieve(model.getCategory())
                            .switchIfEmpty(Mono.just(new Category()));
                    Mono<Interaction> interaction = interactionService.retrieve(model.getId())
                            .switchIfEmpty(Mono.just(new Interaction()));
                    return Mono.zip(creator, category, interaction)
                            .map(tuple -> ArticleUtils
                                    .toEntity(model,
                                            null,
                                            tuple.getT1(),
                                            tuple.getT2(),
                                            tuple.getT3()
                                    ));
                });
    }

    public Mono<Article> retrieve(ServerHttpRequest request, Long id){
        AuthorizationData auth = permissions.extractAuthData(request);
        return repository.findById(id)
                .filter(model -> model.getArticleStatus().equals(ArticleStatus.GRANTED) ||
                        auth.isAdmin() || model.getCreator().equals(auth.getId()))
                .switchIfEmpty(Mono.error(new NotFoundAppException("Article not found")))
                .flatMap(model -> contentRepository.findById(model.getId())
                        .flatMap(content -> {

                            Mono<AppUser> creator = userRepository.retrieve(model.getCreator())
                                    .switchIfEmpty(Mono.just(new AppUser()));
                            Mono<Category> category = categoryService.retrieve(model.getCategory())
                                    .switchIfEmpty(Mono.just(new Category()));
                            Mono<Interaction> interaction = interactionService.retrieve(model.getId())
                                    .switchIfEmpty(Mono.just(new Interaction()));
                            return Mono.zip(creator, category, interaction)
                                    .map(tuple -> ArticleUtils
                                            .toEntity(model,
                                                    content,
                                                    tuple.getT1(),
                                                    tuple.getT2(),
                                                    tuple.getT3())
                                    );
                        })
                );
    }

    public Flux<Article> listRecommended(ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        return recommendationService.getRecommendations(auth.getId())
                .flatMapMany(repository::findAllById)
                .flatMap(model -> {
                    log.info("Mapping model: {}", model);
                    Mono<AppUser> creator = userRepository.retrieve(model.getCreator())
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("Creator not found for ID: {}", model.getCreator());
                                return Mono.empty();
                            }));

                    Mono<Category> category = categoryService.retrieve(model.getCategory())
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("Category not found for ID: {}", model.getId());
                                return Mono.empty();
                            }));

                    Mono<Interaction> interaction = interactionService.retrieve(model.getId())
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("Interaction not found for Article ID: {}", model.getId());
                                return Mono.empty();
                            }));

                    return Mono.zip(creator, category, interaction)
                            .doOnNext(tuple -> log.info("Zipped result: {}", tuple))
                            .map(tuple -> {
                                Article a = ArticleUtils.toEntity(model, null, tuple.getT1(), tuple.getT2(), tuple.getT3());
                                log.info("Article {}", a);
                                return a;
                            })
                            .onErrorResume(e -> {
                                log.error("Mapping failed for model: {}", model, e);
                                return Mono.empty();
                            });
                });
    }

    @Transactional
    public Mono<Article> create(Article article, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!permissions.canPublish(auth)) return Mono.error(new ForbiddenAppException("User can't publish articles"));
        PaidStatus paidStatus = permissions.canBePaid(auth);
        ArticleModel articleModel = new ArticleModel(article.getTitle(), article.getAnnotation(), ArticleStatus.MODERATION, paidStatus, auth.getId(), article.getCategory().getId());
        return repository.save(articleModel)
                .flatMap(model -> contentRepository
                        .save(new ArticleContentModel(model.getId(), article.getContent()))
                        .flatMap(content -> {
                            Mono<AppUser> creator = userRepository.retrieve(model.getCreator());
                            Mono<Category> category = categoryService.retrieve(model.getCategory());
                            Mono<Interaction> interaction = interactionService.retrieve(model.getId());
                            return Mono.zip(creator, category, interaction)
                                    .map(tuple -> ArticleUtils
                                            .toEntity(model,
                                                    content,
                                                    tuple.getT1(),
                                                    tuple.getT2(),
                                                    tuple.getT3())
                                    );
                        }));
                //.onErrorMap(e -> new ServerErrorAppException("Internal server error"));
    }

    @Transactional
    public Mono<Article> update(Article article, Long articleId, ServerHttpRequest request) {
        AuthorizationData auth = permissions.extractAuthData(request);
        return repository.findById(articleId)
                .flatMap(model -> {
                    if (!permissions.canEdit(auth, model.getCreator())) return Mono.error(new ForbiddenAppException("User can't edit article(s)"));
                    if(article.getTitle() != null) model.setTitle(article.getTitle());
                    if(article.getAnnotation() != null) model.setAnnotation(article.getAnnotation());
                    if(article.getCategory() != null) model.setCategory(article.getCategory().getId());
                    Mono<ArticleModel> updatedArticle = repository.save(model);
                    Mono<ArticleContentModel> updatedContent = contentRepository.findById(model.getId())
                            .switchIfEmpty(Mono.error(new NotFoundAppException("Article content not found")))
                            .flatMap(content -> {
                                if(article.getContent() != null){
                                    content.setContent(article.getContent());
                                    return contentRepository.save(content);
                                } else {
                                    return Mono.just(content);
                                }
                            });
                    return Mono.zip(updatedArticle, updatedContent)
                            .flatMap(tuple -> {
                                Mono<AppUser> creator = userRepository.retrieve(model.getCreator());
                                Mono<Category> category = categoryService.retrieve(model.getCategory());
                                Mono<Interaction> interaction = interactionService.retrieve(model.getId());
                                return Mono.zip(creator, category, interaction)
                                        .map(extraTuple -> ArticleUtils.toEntity(
                                                tuple.getT1(),
                                                tuple.getT2(),
                                                extraTuple.getT1(),
                                                extraTuple.getT2(),
                                                extraTuple.getT3()
                                        ));
                            });
                });
    }

    @Transactional
    public Mono<Article> moderate(String status, Long id, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!auth.isAdmin()) return Mono.error(new ForbiddenAppException("Access denied"));
        try{
            ArticleStatus articleStatus = ArticleStatus.valueOf(status);
            return repository.findById(id)
                    .flatMap(model -> {
                        model.setArticleStatus(articleStatus);
                        return repository.save(model)
                                .map(updatedArticle -> ArticleUtils
                                        .toEntity(updatedArticle,
                                                null,
                                                null,
                                                null,
                                                null));
                    });
        } catch (IllegalArgumentException e){
            return Mono.error(new BadRequestAppException("Wrong param"));
        }
    }

    @Transactional
    public Mono<Void> delete(Long articleId, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        return repository.findById(articleId)
                .flatMap(model -> {
                    if(!permissions.canDelete(auth, model.getCreator())){
                        return Mono.error(new ForbiddenAppException("User can't delete article(s)"));
                    }
                    return Mono.when(
                            repository.delete(model.getId()),
                            contentRepository.delete(model.getId())
                    );
                });
    }
}
