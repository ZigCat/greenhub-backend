package com.github.zigcat.greenhub.article_provider.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document(collection = "interactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractionModel {
    @Id
    private String id;

    @Field(targetType = FieldType.INT64)
    private Long userId;

    @Field(targetType = FieldType.INT64)
    private Long articleId;

    private Boolean like;
    private Integer views;
    private Integer rating;

    public InteractionModel(Long userId, Long articleId, Boolean like, Integer views, Integer rating) {
        this.userId = userId;
        this.articleId = articleId;
        this.like = like;
        this.views = views;
        this.rating = rating;
    }
}
