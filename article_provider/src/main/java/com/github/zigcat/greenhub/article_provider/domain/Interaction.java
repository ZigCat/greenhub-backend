package com.github.zigcat.greenhub.article_provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {
    private String id;
    private Long userId;
    private Long articleId;
    private boolean like;
    private boolean star;
    private Integer views;
    private Integer rating;
}
