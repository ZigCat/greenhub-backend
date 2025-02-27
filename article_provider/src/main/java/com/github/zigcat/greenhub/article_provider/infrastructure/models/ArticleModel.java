package com.github.zigcat.greenhub.article_provider.infrastructure.models;

import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("articles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Article Entity")
public class ArticleModel {
    @Id
    @Column("article_id")
    @Schema(example = "1")
    private Long id;

    @Schema(example = "How to create articles on GreenHub")
    private String title;

    @Column("creation_date")
    private LocalDateTime creationDate;

    @Column("article_status")
    @Schema(example = "MODERATION")
    private ArticleStatus articleStatus;

    @Column("paid_status")
    @Schema(example = "FREE")
    private PaidStatus paidStatus;

    @Column("creator_id")
    @Schema(example = "1")
    private Long creator;

    @Column("category_id")
    @Schema(example = "1")
    private Long category;

    public ArticleModel(String title, LocalDateTime creationDate, ArticleStatus articleStatus, PaidStatus paidStatus, Long creator, Long category) {
        this.title = title;
        this.creationDate = creationDate;
        this.articleStatus = articleStatus;
        this.paidStatus = paidStatus;
        this.creator = creator;
        this.category = category;
    }

    public ArticleModel(String title, ArticleStatus articleStatus, PaidStatus paidStatus, Long creator, Long category) {
        this.title = title;
        this.creationDate = LocalDateTime.now();
        this.articleStatus = articleStatus;
        this.paidStatus = paidStatus;
        this.creator = creator;
        this.category = category;
    }
}
