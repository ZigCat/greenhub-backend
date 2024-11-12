package com.github.zigcat.greenhub.api_gateway.security;

import com.github.zigcat.greenhub.api_gateway.security.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    private JwtAuthenticationFilter filter;

    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/register").permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}
