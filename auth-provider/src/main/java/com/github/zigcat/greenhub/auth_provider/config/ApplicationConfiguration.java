package com.github.zigcat.greenhub.auth_provider.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    public ApplicationEventPublisher applicationEventPublisher(){
        return this.applicationEventPublisher;
    }
}
