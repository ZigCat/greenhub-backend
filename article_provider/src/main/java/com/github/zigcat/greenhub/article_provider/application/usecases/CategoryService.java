package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.application.exceptions.ConflictAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.ServerErrorAppException;
import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.CategoryRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.CategoryModel;
import com.github.zigcat.greenhub.article_provider.utils.CategoryUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final PermissionService permissions;

    public CategoryService(
            CategoryRepository repository,
            PermissionService permissions
    ) {
        this.repository = repository;
        this.permissions = permissions;
    }

    public Mono<Category> retrieve(Long id){
        return repository.findById(id)
                .map(CategoryUtils::toEntity);
    }

    public Flux<Category> list(){
        return repository.findAll()
                .map(CategoryUtils::toEntity);
    }

    @Transactional
    public Mono<Category> create(Category category, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!auth.isAdmin()) return Mono.error(new ForbiddenAppException("Access denied"));
        return repository.save(CategoryUtils.toModel(category))
                .map(CategoryUtils::toEntity);
    }

    @Transactional
    public Mono<Category> update(Long id, Category category, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!auth.isAdmin()) return Mono.error(new ForbiddenAppException("Access Denied"));
        return repository.findById(id)
                .flatMap(model -> {
                    if(category.getName() != null) model.setName(category.getName());
                    if(category.getDescription() != null) model.setDescription(category.getDescription());
                    return repository.save(model)
                            .map(CategoryUtils::toEntity);
                });
    }

    @Transactional
    public Mono<Void> delete(Long id, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!auth.isAdmin()) return Mono.error(new ForbiddenAppException("Access denied"));
        return repository.findById(id)
                .flatMap(model -> repository.delete(model.getId())
                        .onErrorMap(e -> {
                            if(e instanceof DataIntegrityViolationException){
                                throw new ConflictAppException("Unsafe deletion, denied");
                            }
                            throw new ServerErrorAppException("Unknown error");
                        }));
    }
}
