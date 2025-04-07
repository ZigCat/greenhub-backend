package com.github.zigcat.greenhub.api_gateway.infrastructure.adapters;

import com.github.zigcat.greenhub.api_gateway.domain.AppUser;
import com.github.zigcat.greenhub.api_gateway.domain.interfaces.MessageQueryAdapter;
import com.github.zigcat.greenhub.api_gateway.domain.schemas.TokenType;
import com.github.zigcat.greenhub.api_gateway.domain.MessageTemplate;
import com.github.zigcat.greenhub.api_gateway.exceptions.CoreException;
import com.github.zigcat.greenhub.api_gateway.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.api_gateway.infrastructure.exceptions.ServerErrorInfrastructureException;
import com.github.zigcat.greenhub.api_gateway.infrastructure.exceptions.ServiceUnavailableInfrastructureException;
import com.github.zigcat.greenhub.api_gateway.infrastructure.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private KafkaSender<String, MessageTemplate<InfrastructureDTO.JwtDTO>> jwtSender;
    private KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserAuth>> userAuthReceiver;

    public KafkaMessageQueryAdapter(
            KafkaSender<String, MessageTemplate<InfrastructureDTO.JwtDTO>> jwtSender,
            KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserAuth>> userAuthReceiver
    ) {
        this.jwtSender = jwtSender;
        this.userAuthReceiver = userAuthReceiver;
    }

    @Override
    public Mono<AppUser> performAndAwait(String token, TokenType tokenType) {
        log.info("AUTHORIZING REQUEST");
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<InfrastructureDTO.JwtDTO> requestData =
                new MessageTemplate<>(new InfrastructureDTO.JwtDTO(token, tokenType));
        ProducerRecord<String, MessageTemplate<InfrastructureDTO.JwtDTO>> request =
                new ProducerRecord<>("auth-topic", correlationId, requestData);
        log.info("Sending and awaiting Kafka Record...");
        return jwtSender.send(Mono.just(SenderRecord.create(request, correlationId)))
                .doOnNext(result -> log.info("Message sent successfully with correlationId: {}", correlationId))
                .onErrorResume(e -> {
                    log.error("Error while sending Kafka message ", e);
                    return Mono.error(new ServerErrorInfrastructureException("Unable to interact with user service"));
                })
                .then(userAuthReceiver.receive()
                        .doOnNext(record -> log.info("Received record with key: {}", record.key()))
                        .filter(record -> {
                            if(record.key().equals(correlationId)){
                                log.info("Correlated response captured");
                                int status = record.value().getStatus();
                                if(status == 200){
                                    log.info("Status 200, reading...");
                                    return true;
                                } else {
                                    log.error("Error occurred {}", record.value());
                                    throw new CoreException(record.value().getMessage(), status);
                                }
                            }
                            return false;
                        })
                        .map(record -> UserMapper.toEntity(record.value().getPayload()))
                        .timeout(Duration.ofSeconds(5))
                        .next()
                        .onErrorResume(e -> {
                            log.error("Error receiving Kafka response", e);
                            if(e instanceof CoreException){
                                return Mono.error(new CoreException(e.getMessage(), ((CoreException) e).getCode()));
                            }
                            return Mono.error(new ServiceUnavailableInfrastructureException("User service unavailable"));
                        })
                );
    }
}
