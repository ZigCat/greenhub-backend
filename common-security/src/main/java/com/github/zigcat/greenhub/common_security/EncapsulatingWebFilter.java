package com.github.zigcat.greenhub.common_security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class EncapsulatingWebFilter implements WebFilter {
    private final Logger log = LoggerFactory.getLogger(EncapsulatingWebFilter.class);

    @Value("${greenhub.common-security.api-key}")
    private String API_KEY;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Processing request via EncapsulatingWebFilter");
        log.info("Stage 1: Route filtering");
        String path = exchange.getRequest().getPath().value();
        if(!path.startsWith("/webclient/")){
            log.info("Stage 2: Source filtering");
            String source = exchange.getRequest().getHeaders().getFirst("X-Request-Source");
            if(!"Gateway".equals(source)){
                log.info("Processing ended with STATUS=403, completing response...");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        }
        log.info("Stage 2: Authorizing");
        String internalHeader = exchange.getRequest().getHeaders().getFirst("X-Internal-Token");
        if (!API_KEY.equals(internalHeader)) {
            log.info("Processing ended with STATUS=403, completing response...");
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
