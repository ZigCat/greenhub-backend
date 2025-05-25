package com.github.zigcat.greenhub.article_provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArticleProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticleProviderApplication.class, args);
	}

}
