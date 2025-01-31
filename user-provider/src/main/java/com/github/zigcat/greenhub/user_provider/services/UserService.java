package com.github.zigcat.greenhub.user_provider.services;

import com.github.zigcat.greenhub.user_provider.dto.mq.responses.UserAuthResponse;
import com.github.zigcat.greenhub.user_provider.dto.rest.entities.UserDTO;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.entities.Scope;
import com.github.zigcat.greenhub.user_provider.repositories.ScopeRepository;
import com.github.zigcat.greenhub.user_provider.repositories.UserRepository;
import com.github.zigcat.greenhub.user_provider.utils.UserUtils;
import io.r2dbc.spi.Row;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ScopeRepository scopeRepository;

    @Autowired
    public UserService(UserRepository userRepository, ScopeRepository scopeRepository) {
        this.userRepository = userRepository;
        this.scopeRepository = scopeRepository;
    }

    public Flux<AppUser> list(){
        return userRepository.findAll();
    }

    public Mono<AppUser> retrieve(Long userId){
        return userRepository.findById(userId);
    }

    public Mono<UserAuthResponse> retrieveByIdWithScopes(Long id){
        return userRepository.findUserByIdWithScopes(id)
                .as(UserUtils::mapAuthRows)
                .defaultIfEmpty(new UserAuthResponse());
    }

    public Mono<UserAuthResponse> retrieveByEmailWithScopes(String email){
        return userRepository.findUserByEmailWithScopes(email)
                .as(UserUtils::mapAuthRows)
                .defaultIfEmpty(new UserAuthResponse());
    }

    public Mono<AppUser> retrieveByEmail(String email){
        return userRepository.findByEmail(email)
                .defaultIfEmpty(new AppUser());
    }

    public Mono<AppUser> create(Mono<UserDTO> userDTO){
        return userDTO
                .map(dto -> {
                    log.info("Registering user...");
                    return UserUtils.toUser(dto);
                })
                .flatMap(user -> {
                    user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
                    return userRepository.save(user)
                            .flatMap(savedUser -> {
                                List<Scope> defaultScopes = UserUtils.defaultUserScopes(savedUser.getId());
                                return scopeRepository.saveAll(defaultScopes)
                                        .then(Mono.just(savedUser));
                            })
                            .doOnError(e -> log.error("Error while saving user to DB ", e));
                });
    }
}
