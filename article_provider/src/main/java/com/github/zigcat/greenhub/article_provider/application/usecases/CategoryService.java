package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.application.exceptions.ConflictAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.article_provider.application.exceptions.ServerErrorAppException;
import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.CategoryRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.CategoryModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.utils.CategoryUtils;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
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

    private Mono<Category> retrieve(Long id){
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundAppException("Category not found")))
                .map(CategoryUtils::toEntity);
    }

    public Mono<Category> listById(Long id){
        return retrieve(id);
    }

    public Flux<Category> list(){
        return repository.findAll()
                .map(CategoryUtils::toEntity);
    }

    @Transactional
    public Mono<Category> create(DTO.CategoryCreateDTO dto, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!auth.isAdmin()) return Mono.error(new ForbiddenAppException("Access denied"));
        return repository.save(new CategoryModel(dto.name()))
                .map(CategoryUtils::toEntity);
    }

    @Transactional
    public Mono<Category> update(Long id, DTO.CategoryCreateDTO dto, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!auth.isAdmin()) return Mono.error(new ForbiddenAppException("Access Denied"));
        return retrieve(id)
                .flatMap(entity -> {
                    entity.setName(dto.name());
                    return repository.save(CategoryUtils.toModel(entity))
                            .map(CategoryUtils::toEntity);
                });
    }

    @Transactional
    public Mono<Void> delete(Long id, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!auth.isAdmin()) return Mono.error(new ForbiddenAppException("Access denied"));
        return retrieve(id)
                .flatMap(entity -> repository.delete(entity.getId())
                        .onErrorMap(e -> {
                            if(e instanceof DataIntegrityViolationException){
                                throw new ConflictAppException("Unsafe deletion, denied");
                            }
                            throw new ServerErrorAppException("Unknown error");
                        }));
    }
}
