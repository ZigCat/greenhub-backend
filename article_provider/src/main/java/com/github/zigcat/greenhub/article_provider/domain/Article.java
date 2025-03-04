package com.github.zigcat.greenhub.article_provider.domain;

import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime creationDate;
    private ArticleStatus articleStatus;
    private PaidStatus paidStatus;
    private AppUser creator;
    private Category category;
    private Interaction interaction;

    public Article(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
