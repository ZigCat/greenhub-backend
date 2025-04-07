package com.github.zigcat.greenhub.auth_provider.application.usecases;

import com.github.zigcat.greenhub.auth_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.auth_provider.application.exceptions.UnauthorizedAppException;
import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.domain.JwtToken;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.MessageQueryAdapter;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.SecurityProvider;
import com.github.zigcat.greenhub.auth_provider.domain.schemas.TokenType;
import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.auth_provider.domain.JwtData;
import com.github.zigcat.greenhub.auth_provider.application.events.AuthorizeEvent;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.JwtAuthInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AuthService {
    private final MessageQueryAdapter messageQueryAdapter;
    private final SecurityProvider securityProvider;

    public AuthService(MessageQueryAdapter messageQueryAdapter, SecurityProvider securityProvider) {
        this.messageQueryAdapter = messageQueryAdapter;
        this.securityProvider = securityProvider;
    }

    @EventListener
    public void handleAuthorizeEvent(AuthorizeEvent event){
        CompletableFuture<InfrastructureDTO.UserAuth> replyFuture =
                event.getReplyFuture();
        processAuthorization(event.getToken())
                .doOnNext(entity -> {
                    replyFuture.complete(UserMapper.toAuthDTO(entity));
                })
                .onErrorResume(e -> {
                    replyFuture.completeExceptionally(e);
                    return Mono.empty();
                })
                .subscribe();
    }

    private Mono<AppUser> processAuthorization(String token) throws JwtAuthInfrastructureException {
        return Mono.fromRunnable(() -> securityProvider.validateAccessToken(token))
                .then(Mono.fromCallable(() -> securityProvider.getAccessSubject(token)))
                .flatMap(messageQueryAdapter::authorizeAndAwait);
    }

    public Mono<AppUser> register(AppUser user){
        return messageQueryAdapter.registerAndAwait(user);
    }

    public Mono<JwtData> login(ServerHttpRequest request){
        AppUser user = extractAuthData(request);
        return messageQueryAdapter.loginAndAwait(user)
                .map(res ->
                        new JwtData(
                                securityProvider.generateAccessToken(res),
                                securityProvider.generateRefreshToken(res),
                                res
                        )
                );
    }

    public Mono<JwtData> refresh(JwtToken data){
        if(data == null
                || data.getToken() == null
                || data.getTokenType() != TokenType.REFRESH)
            return Mono.error(new BadRequestAppException("Missing or invalid token"));
        String token = data.getToken();
        return Mono.fromCallable(() -> {
                    securityProvider.validateRefreshToken(token);
                    return securityProvider.getRefreshClaims(token);
                })
                .onErrorResume(e -> Mono.error(new UnauthorizedAppException(e.getMessage())))
                .subscribeOn(Schedulers.boundedElastic())
                .map(user -> new JwtData(
                        securityProvider.generateAccessToken(user),
                        securityProvider.generateRefreshToken(user),
                        null
                ));
    }

    private AppUser extractAuthData(ServerHttpRequest request){
        log.info("Decoding Auth Data");
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);
            String username = parts[0];
            String password = parts[1];
            return new AppUser(username, password);
        }
        throw new UnauthorizedAppException("Unauthorized access");
    }
}
