package com.github.zigcat.greenhub.auth_provider;

import com.github.zigcat.greenhub.auth_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.auth_provider.kafka.adapter.KafkaMessageQueryAdapter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthProviderApplication implements CommandLineRunner {
	private MessageQueryAdapter adapter;

	@Autowired
	public AuthProviderApplication(MessageQueryAdapter adapter) {
		this.adapter = adapter;
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthProviderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		adapter.processMessage();
	}
}
