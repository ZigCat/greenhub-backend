package com.github.zigcat.greenhub.user_provider;

import com.github.zigcat.greenhub.user_provider.application.usecases.MessageQueryService;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.MessageQueryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserProviderApplication implements CommandLineRunner {
	private final MessageQueryService mqService;

	public UserProviderApplication(MessageQueryService mqService) {
		this.mqService = mqService;
	}

	public static void main(String[] args) {
		SpringApplication.run(UserProviderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mqService.startProcessing();
	}
}
