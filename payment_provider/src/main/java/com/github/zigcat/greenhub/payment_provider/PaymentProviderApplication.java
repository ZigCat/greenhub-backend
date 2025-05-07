package com.github.zigcat.greenhub.payment_provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaymentProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentProviderApplication.class, args);
	}

}
