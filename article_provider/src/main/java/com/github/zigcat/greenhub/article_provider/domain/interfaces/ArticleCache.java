package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.Article;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ArticleCache {
    Flux<Article> getCachedArticles();
    Mono<Void> cacheArticles(List<Article> articles);
}
