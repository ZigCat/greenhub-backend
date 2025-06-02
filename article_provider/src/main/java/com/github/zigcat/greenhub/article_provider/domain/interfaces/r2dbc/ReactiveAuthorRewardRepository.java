package com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.AuthorRewardModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface ReactiveAuthorRewardRepository extends ReactiveCrudRepository<AuthorRewardModel, Long> {
    @Query("SELECT * FROM author_reward WHERE author_id = :authorId")
    Flux<AuthorRewardModel> findAllByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT * FROM author_reward WHERE calculated_at > :date")
    Flux<AuthorRewardModel> findAllByCalculatedAtAfter(@Param("date") LocalDateTime date);
}
