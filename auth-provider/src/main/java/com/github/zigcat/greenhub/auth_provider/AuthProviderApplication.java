package com.github.zigcat.greenhub.auth_provider;

import com.github.zigcat.greenhub.auth_provider.application.usecases.MessageQueryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthProviderApplication implements CommandLineRunner {
	private MessageQueryService mqService;

	public AuthProviderApplication(MessageQueryService mqService) {
		this.mqService = mqService;
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthProviderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mqService.startProcessing();
	}
}
