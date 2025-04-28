package com.github.zigcat.greenhub.auth_provider.application.usecases;

import com.github.zigcat.greenhub.auth_provider.domain.UserKey;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.UserKeyRepository;
import com.github.zigcat.greenhub.auth_provider.infrastructure.mappers.KeyMapper;
import com.github.zigcat.greenhub.auth_provider.infrastructure.models.UserKeyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class KeyService {
    private final UserKeyRepository repository;

    public KeyService(UserKeyRepository repository) {
        this.repository = repository;
    }

    public Mono<UserKey> generateKeyPair(String username){
        return Mono.fromCallable(() -> {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(2048);
            return keygen.generateKeyPair();
        }).flatMap(keyPair -> repository.save(
                new UserKeyModel(
                    username,
                    encodeKey(keyPair.getPublic()),
                    encodeKey(keyPair.getPrivate())
                )).map(KeyMapper::toEntity));
    }

    Mono<UserKey> retrieve(Long id){
        return repository.findById(id).map(KeyMapper::toEntity);
    }

    Mono<UserKey> retrieveByUsername(String username){
        return repository.findByUsername(username).map(KeyMapper::toEntity);
    }

    Mono<Void> delete(String username){
        return repository.delete(username);
    }

    private String encodeKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public PublicKey decodePublicKey(String base64PublicKey) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode public key", e);
        }
    }

    public PrivateKey decodePrivateKey(String base64PrivateKey) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode private key", e);
        }
    }
}
