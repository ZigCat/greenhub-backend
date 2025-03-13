package com.github.zigcat.greenhub.article_provider.domain.schemas;

import lombok.Getter;

@Getter
public enum ArticleStatus {
    GRANTED("GRANTED"),
    DENIED("DENIED"),
    MODERATION("MODERATION");

    private String value;

    ArticleStatus(String value) {
        this.value = value;
    }
}
