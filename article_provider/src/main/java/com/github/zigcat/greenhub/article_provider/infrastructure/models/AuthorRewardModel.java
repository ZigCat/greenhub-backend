package com.github.zigcat.greenhub.article_provider.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("author_reward")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRewardModel {
    @Id
    @Column("reward_id")
    private Long id;
    @Column("author_id")
    private Long authorId;
    private Double reward;
    @Column("calculated_at")
    private LocalDateTime calculatedAt;

    public AuthorRewardModel(Long authorId, Double reward, LocalDateTime calculatedAt) {
        this.authorId = authorId;
        this.reward = reward;
        this.calculatedAt = calculatedAt;
    }
}
