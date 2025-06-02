package com.github.zigcat.greenhub.article_provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorReward {
    private Long id;
    private Long authorId;
    private Double reward;
    private LocalDateTime calculatedAt;

    public AuthorReward(Long authorId, Double reward, LocalDateTime calculatedAt) {
        this.authorId = authorId;
        this.reward = reward;
        this.calculatedAt = calculatedAt;
    }
}
