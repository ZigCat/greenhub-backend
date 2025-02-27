package com.github.zigcat.greenhub.article_provider.infrastructure.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Category Entity")
public class CategoryModel {
    @Id
    @Column("category_id")
    @Schema(example = "1")
    private Long id;

    @Schema(example = "Pollution")
    private String name;

    public CategoryModel(String name) {
        this.name = name;
    }
}
