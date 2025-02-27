package com.github.zigcat.greenhub.article_provider.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document(collection = "article_content")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleContentModel {
    @Id
    private String id;
    @Field(targetType = FieldType.INT64)
    private Long articleId;
    private String content;

    public ArticleContentModel(Long articleId, String content) {
        this.articleId = articleId;
        this.content = content;
    }
}
