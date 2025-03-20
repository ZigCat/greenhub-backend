package com.github.zigcat.greenhub.article_provider.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Category domain model")
public class Category {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "Ecology")
    private String name;

    public Category(Long id) {
        this.id = id;
    }

    public Category(String name) {
        this.name = name;
    }
}
