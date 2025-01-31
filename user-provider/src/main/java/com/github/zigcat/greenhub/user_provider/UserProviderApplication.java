package com.github.zigcat.greenhub.user_provider;

import com.github.zigcat.greenhub.user_provider.adapters.MessageQueryAdapter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserProviderApplication implements CommandLineRunner {
	private MessageQueryAdapter adapter;

	@Autowired
	public UserProviderApplication(MessageQueryAdapter adapter) {
		this.adapter = adapter;
	}

	public static void main(String[] args) {
		SpringApplication.run(UserProviderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		adapter.processRegisterMessage();
		adapter.processAuthorizeMessage();
		adapter.processLoginMessage();
	}
}
