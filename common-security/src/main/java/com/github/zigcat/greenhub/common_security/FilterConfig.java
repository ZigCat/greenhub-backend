package com.github.zigcat.greenhub.common_security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public EncapsulatingWebFilter encapsulatingWebFilter(){
        return new EncapsulatingWebFilter();
    }
}
