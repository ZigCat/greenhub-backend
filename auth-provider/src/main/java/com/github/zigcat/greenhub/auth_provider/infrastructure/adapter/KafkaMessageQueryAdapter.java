package com.github.zigcat.greenhub.auth_provider.infrastructure.adapter;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.MessageQueryAdapter;
import com.github.zigcat.greenhub.auth_provider.application.events.AuthorizeEvent;
import com.github.zigcat.greenhub.auth_provider.domain.MessageTemplate;
import com.github.zigcat.greenhub.auth_provider.exceptions.ClientErrorException;
import com.github.zigcat.greenhub.auth_provider.exceptions.CoreException;
import com.github.zigcat.greenhub.auth_provider.exceptions.ServerErrorException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.ServerErrorInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.ServiceUnavailableInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaSender<String, MessageTemplate<InfrastructureDTO.UserRegister>> registerSender;
    private final KafkaReceiver<String, MessageTemplate<AppUser>> userRegisterReceiver;
    private final KafkaSender<String, MessageTemplate<String>> authSender;
    private final KafkaReceiver<String, MessageTemplate<AppUser>> userAuthReceiver;
    private final KafkaSender<String, MessageTemplate<InfrastructureDTO.UserLogin>> loginSender;
    private final KafkaReceiver<String, MessageTemplate<AppUser>> userLoginReceiver;
    private final KafkaReceiver<String, MessageTemplate<InfrastructureDTO.JwtDTO>> jwtReceiver;
    private final KafkaSender<String, MessageTemplate<InfrastructureDTO.UserAuth>> userGatewaySender;

    public KafkaMessageQueryAdapter(
            ApplicationEventPublisher applicationEventPublisher,
            KafkaSender<String, MessageTemplate<InfrastructureDTO.UserRegister>> registerSender,
            KafkaReceiver<String, MessageTemplate<AppUser>> userRegisterReceiver,
            KafkaSender<String, MessageTemplate<String>> authSender,
            KafkaReceiver<String, MessageTemplate<AppUser>> userAuthReceiver,
            KafkaSender<String, MessageTemplate<InfrastructureDTO.UserLogin>> loginSender,
            KafkaReceiver<String, MessageTemplate<AppUser>> userLoginReceiver,
            KafkaReceiver<String, MessageTemplate<InfrastructureDTO.JwtDTO>> jwtReceiver,
            KafkaSender<String, MessageTemplate<InfrastructureDTO.UserAuth>> userGatewaySender
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.registerSender = registerSender;
        this.userRegisterReceiver = userRegisterReceiver;
        this.authSender = authSender;
        this.userAuthReceiver = userAuthReceiver;
        this.loginSender = loginSender;
        this.userLoginReceiver = userLoginReceiver;
        this.jwtReceiver = jwtReceiver;
        this.userGatewaySender = userGatewaySender;
    }

    @Override
    public void processMessage() {
        jwtReceiver.receive()
                .bufferTimeout(10, Duration.ofMillis(500))
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .doOnNext(batch -> {
                    batch.forEach(record -> {
                        String correlationId = record.key();
                        log.info("Received message with id {}", correlationId);
                        processRequest(record.value().getPayload(), correlationId);
                    });
                })
                .doOnError(e -> log.error("Error while receiving message", e))
                .subscribe();
    }

    private void processRequest(InfrastructureDTO.JwtDTO dto, String correlationId) {
        CompletableFuture<InfrastructureDTO.UserAuth> replyFuture =
                new CompletableFuture<>();
        AuthorizeEvent event =
                new AuthorizeEvent(this, dto.token(), replyFuture);
        log.info("Preparing and publishing event for AuthService");
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            log.info("Reply accepted, preparing response");
            MessageTemplate<InfrastructureDTO.UserAuth> responseMessage = new MessageTemplate<>(reply);
            sendResponse(responseMessage, correlationId);
        }).exceptionally(e -> {
            int status;
            if(e instanceof CoreException){
                status = ((CoreException) e).getCode();
            } else {
                status = 500;
            }
            MessageTemplate<InfrastructureDTO.UserAuth> responseData = new MessageTemplate<>(status, e.getMessage());
            sendResponse(responseData, correlationId);
            return null;
        });
    }

    private void sendResponse(MessageTemplate<InfrastructureDTO.UserAuth> responseMessage, String correlationId) {
        log.info("Sending response back");
        ProducerRecord<String, MessageTemplate<InfrastructureDTO.UserAuth>> response =
                new ProducerRecord<>("auth-topic-reply", correlationId, responseMessage);
        userGatewaySender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(data -> log.info("Response sent successfully"))
                .doOnError(e -> log.error("Error sending response: ", e))
                .subscribe();
    }

    @Override
    public Mono<AppUser> registerAndAwait(AppUser data) {
        log.info("REGISTER REQUEST");
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<InfrastructureDTO.UserRegister> requestData =
                new MessageTemplate<>(UserMapper.toRegisterDTO(data));
        ProducerRecord<String, MessageTemplate<InfrastructureDTO.UserRegister>> request =
                new ProducerRecord<>("user-reg-topic", correlationId, requestData);
        log.info("Sending and awaiting Kafka Record...");
        return registerSender.send(Mono.just(SenderRecord.create(request, correlationId)))
                .doOnNext(result -> log.info("Message sent successfully with correlationId: {}", correlationId))
                .onErrorResume(e -> {
                    log.error("Error while sending Kafka message ", e);
                    return Mono.error(new ServerErrorInfrastructureException("Unable to interact with user service"));
                })
                .then(userRegisterReceiver.receive()
                        .doOnNext(record -> log.info("Received record with key: {}", record.key()))
                        .filter(record -> {
                            if(record.key().equals(correlationId)){
                                log.info("Correlated response captured");
                                int status = record.value().getStatus();
                                if(status == 200){
                                    log.info("Status 200, reading...");
                                    return true;
                                } else if(status / 100 == 4){
                                    throw new ClientErrorException(record.value().getMessage(), status);
                                } else if(status / 100 == 5){
                                    throw new ServerErrorException(record.value().getMessage(), status);
                                }
                            }
                            return false;
                        })
                        .map(record -> record.value().getPayload())
                        .timeout(Duration.ofSeconds(5))
                        .next()
                        .onErrorResume(e -> {
                            log.error("Error receiving Kafka response", e);
                            if(e instanceof ServerErrorException se){
                                return Mono.error(new ServerErrorException(se.getMessage(), se.getCode()));
                            } else if(e instanceof ClientErrorException ce){
                                return Mono.error(new ClientErrorException(ce.getMessage(), ce.getCode()));
                            }
                            return Mono.error(new ServiceUnavailableInfrastructureException("User service unavailable"));
                        })
                );
    }

    @Override
    public Mono<AppUser> authorizeAndAwait(String data) {
        log.info("AUTHORIZING REQUEST");
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<String> requestData = new MessageTemplate<>(data);
        ProducerRecord<String, MessageTemplate<String>> request =
                new ProducerRecord<>("user-auth-topic", correlationId, requestData);
        log.info("Sending and awaiting Kafka Record...");
        return authSender.send(Mono.just(SenderRecord.create(request, correlationId)))
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
                                } else if(status / 100 == 4){
                                    throw new ClientErrorException(record.value().getMessage(), status);
                                } else if(status / 100 == 5){
                                    throw new ServerErrorException(record.value().getMessage(), status);
                                }
                            }
                            return false;
                        })
                        .map(record -> record.value().getPayload())
                        .timeout(Duration.ofSeconds(5))
                        .next()
                        .onErrorResume(e -> {
                            log.error("Error receiving Kafka response", e);
                            if(e instanceof ServerErrorException){
                                return Mono.error(new ServerErrorException(e.getMessage(), ((ServerErrorException) e).getCode()));
                            } else if(e instanceof ClientErrorException ce){
                                return Mono.error(new ClientErrorException(ce.getMessage(), ce.getCode()));
                            }
                            return Mono.error(new ServiceUnavailableInfrastructureException("User service unavailable"));
                        })
                );
    }

    @Override
    public Mono<AppUser> loginAndAwait(AppUser data) {
        log.info("LOGIN REQUEST");
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<InfrastructureDTO.UserLogin> requestMessage =
                new MessageTemplate<>(UserMapper.toLoginDTO(data));
        ProducerRecord<String, MessageTemplate<InfrastructureDTO.UserLogin>> request =
                new ProducerRecord<>("user-login-topic", correlationId, requestMessage);
        log.info("Sending and awaiting Kafka Record...");
        return loginSender.send(Mono.just(SenderRecord.create(request, correlationId)))
                .doOnNext(result -> log.info("Message sent successfully with correlationId: {}", correlationId))
                .onErrorResume(e -> {
                    log.error("Error while sending Kafka message ", e);
                    return Mono.error(new ServerErrorInfrastructureException("Unable to interact with user service"));
                })
                .then(userLoginReceiver.receive()
                        .filter(record -> {
                            if(record.key().equals(correlationId)){
                                log.info("Correlated response captured");
                                int status = record.value().getStatus();
                                if(status == 200){
                                    log.info("Status 200, reading...");
                                    return true;
                                } else if(status / 100 == 4){
                                    log.warn("Status 400");
                                    throw new ClientErrorException(record.value().getMessage(), status);
                                } else if(status / 100 == 5){
                                    log.warn("Status 500");
                                    throw new ServerErrorException(record.value().getMessage(), status);
                                }
                            }
                            return false;
                        })
                        .map(record -> record.value().getPayload())
                        .timeout(Duration.ofSeconds(5))
                        .next()
                        .onErrorResume(e -> {
                            log.error("Error receiving Kafka response", e);
                            if(e instanceof ServerErrorException se){
                                return Mono.error(new ServerErrorException(se.getMessage(), se.getCode()));
                            } else if(e instanceof ClientErrorException ce){
                                return Mono.error(new ClientErrorException(ce.getMessage(), ce.getCode()));
                            }
                            return Mono.error(new ServiceUnavailableInfrastructureException("User service unavailable"));
                        })
            );
    }
}
