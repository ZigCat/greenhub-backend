package com.github.zigcat.greenhub.auth_provider;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
//		"com.github.zigcat.greenhub.common_security",
		"com.github.zigcat.greenhub.auth_provider"
})
public class AuthProviderApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthProviderApplication.class, args);
	}
}
