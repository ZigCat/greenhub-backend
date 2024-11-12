package com.github.zigcat.greenhub.user_provider.services;

import com.github.zigcat.greenhub.user_provider.dto.UserDTO;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.repositories.UserRepository;
import com.github.zigcat.greenhub.user_provider.utils.UserUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Flux<AppUser> list(){
        return repository.findAll();
    }

    public Mono<AppUser> retrieve(Long userId){
        return repository.findById(userId);
    }

    public Mono<AppUser> retrieveByEmail(String email){
        return repository.findByEmail(email);
    }

    public Mono<AppUser> create(Mono<UserDTO> userDTO){
        return userDTO.map(UserUtils::toUser).flatMap(repository::save);
    }
}
