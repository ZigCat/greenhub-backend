package com.github.zigcat.greenhub.article_provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Long id;
    private String name;

    public Category(Long id) {
        this.id = id;
    }

    public Category(String name) {
        this.name = name;
    }
}
