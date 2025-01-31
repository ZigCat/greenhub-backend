package com.github.zigcat.greenhub.auth_provider.kafka.adapter;

import com.github.zigcat.greenhub.auth_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.AuthorizeRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.LoginRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.auth_provider.events.replies.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.auth_provider.events.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.auth_provider.dto.mq.template.MessageTemplate;
import com.github.zigcat.greenhub.auth_provider.exceptions.JwtAuthException;
import com.github.zigcat.greenhub.auth_provider.exceptions.ServerException;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaReceiver<String, MessageTemplate<JwtRequest>> kafkaJwtRequestReceiver;
    private final KafkaReceiver<String, MessageTemplate<RegisterResponse>> kafkaRegisterResponseReceiver;
    private final KafkaSender<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseSender;
    private final KafkaSender<String, MessageTemplate<RegisterRequest>> kafkaRegisterRequestSender;
    private final KafkaSender<String, MessageTemplate<AuthorizeRequest>> kafkaAuthorizeRequestSender;
    private final KafkaReceiver<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseReceiver;
    private final KafkaSender<String, MessageTemplate<LoginRequest>> kafkaLoginRequestSender;
    private final KafkaReceiver<String, MessageTemplate<UserAuthResponse>> kafkaLoginResponseReceiver;

    @Autowired
    public KafkaMessageQueryAdapter(
            ApplicationEventPublisher applicationEventPublisher,
            KafkaReceiver<String, MessageTemplate<JwtRequest>> kafkaJwtRequestReceiver,
            KafkaReceiver<String, MessageTemplate<RegisterResponse>> kafkaRegisterResponseReceiver,
            KafkaSender<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseSender,
            KafkaSender<String, MessageTemplate<RegisterRequest>> kafkaRegisterRequestSender,
            KafkaSender<String, MessageTemplate<AuthorizeRequest>> kafkaAuthorizeRequestSender,
            KafkaReceiver<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseReceiver,
            KafkaSender<String, MessageTemplate<LoginRequest>> kafkaLoginRequestSender,
            KafkaReceiver<String, MessageTemplate<UserAuthResponse>> kafkaLoginResponseReceiver
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.kafkaJwtRequestReceiver = kafkaJwtRequestReceiver;
        this.kafkaRegisterResponseReceiver = kafkaRegisterResponseReceiver;
        this.kafkaUserResponseSender = kafkaUserResponseSender;
        this.kafkaRegisterRequestSender = kafkaRegisterRequestSender;
        this.kafkaAuthorizeRequestSender = kafkaAuthorizeRequestSender;
        this.kafkaUserResponseReceiver = kafkaUserResponseReceiver;
        this.kafkaLoginRequestSender = kafkaLoginRequestSender;
        this.kafkaLoginResponseReceiver = kafkaLoginResponseReceiver;
    }

    @Override
    public void processMessage() {
        kafkaJwtRequestReceiver.receive()
                .doOnNext(record -> {
                    String correlationId = record.key();
                    log.info("Received message with id {}", correlationId);
                    JwtRequest requestData = record.value().getPayload();
                    processRequest(requestData, correlationId);
                })
                .doOnError(e -> log.error("Error while receiving message", e))
                .subscribe();
    }

    @Override
    public Mono<RegisterResponse> registerAndAwait(RegisterRequest data) {
        log.info("REGISTER REQUEST");
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<RegisterRequest> requestData = new MessageTemplate<>(data);
        ProducerRecord<String, MessageTemplate<RegisterRequest>> request =
                new ProducerRecord<>("user-reg-topic", correlationId, requestData);
        log.info("Sending and awaiting Kafka Record...");
        return kafkaRegisterRequestSender.send(Mono.just(SenderRecord.create(request, correlationId)))
                .doOnNext(result -> log.info("Message sent successfully with correlationId: {}", correlationId))
                .then(kafkaRegisterResponseReceiver.receive()
                        .doOnNext(record -> log.info("Received record with key: {}", record.key()))
                        .filter(record -> {
                            if(record.key().equals(correlationId)){
                                log.info("Correlated response captured");
                                if(record.value().getStatus() == 200){
                                    log.info("Status 200, reading...");
                                    return true;
                                } else {
                                    throw new ServerException(record.value().getMessage());
                                }
                            }
                            return false;
                        })
                        .map(record -> record.value().getPayload())
                        .timeout(Duration.ofSeconds(5))
                        .next())
                        .doOnError(e -> log.error("Error receiving Kafka response", e)
                );
    }

    @Override
    public Mono<UserAuthResponse> authorizeAndAwait(AuthorizeRequest data) {
        log.info("AUTHORIZING REQUEST");
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<AuthorizeRequest> requestData = new MessageTemplate<>(data);
        ProducerRecord<String, MessageTemplate<AuthorizeRequest>> request =
                new ProducerRecord<>("user-auth-topic", correlationId, requestData);
        log.info("Sending and awaiting Kafka Record...");
        return kafkaAuthorizeRequestSender.send(Mono.just(SenderRecord.create(request, correlationId)))
                .doOnNext(result -> log.info("Message sent successfully with correlationId: {}", correlationId))
                .then(kafkaUserResponseReceiver.receive()
                        .doOnNext(record -> log.info("Received record with key: {}", record.key()))
                        .filter(record -> {
                            if(record.key().equals(correlationId)){
                                log.info("Correlated response captured");
                                if(record.value().getStatus() == 200){
                                    log.info("Status 200, reading...");
                                    return true;
                                } else {
                                    throw new ServerException(record.value().getMessage());
                                }
                            }
                            return false;
                        })
                        .map(record -> record.value().getPayload())
                        .timeout(Duration.ofSeconds(5))
                        .next()
                        .doOnError(e -> log.error("Error receiving Kafka response", e))
                );
    }

    @Override
    public Mono<UserAuthResponse> loginAndAwait(LoginRequest data) {
        log.info("LOGIN REQUEST");
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<LoginRequest> requestMessage = new MessageTemplate<>(data);
        ProducerRecord<String, MessageTemplate<LoginRequest>> request =
                new ProducerRecord<>("user-login-topic", correlationId, requestMessage);
        log.info("Sending and awaiting Kafka Record...");
        return kafkaLoginRequestSender.send(Mono.just(SenderRecord.create(request, correlationId)))
                .doOnNext(result -> log.info("Message sent successfully with correlationId: {}", correlationId))
                .then(kafkaLoginResponseReceiver.receive()
                        .filter(record -> {
                            if(record.key().equals(correlationId)){
                                log.info("Correlated response captured");
                                if(record.value().getStatus() == 200){
                                    log.info("Status 200, reading...");
                                    return true;
                                } else {
                                    throw new ServerException(record.value().getMessage());
                                }
                            }
                            return false;
                        })
                        .map(record -> record.value().getPayload())
                        .timeout(Duration.ofSeconds(5))
                        .next()
                        .doOnError(e -> log.error("Error receiving Kafka response", e))
            );
    }

    private void processRequest(JwtRequest requestData, String correlationId) {
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture =
                new CompletableFuture<>();
        AuthorizeMessageQueryAdapterEvent event =
                new AuthorizeMessageQueryAdapterEvent(this, requestData, replyFuture);
        log.info("Preparing and publishing event for AuthService");
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            log.info("Reply accepted, preparing response");
            UserAuthResponse response = reply.getUserResponse();
            MessageTemplate<UserAuthResponse> responseMessage = new MessageTemplate<>(response);
            sendResponse(responseMessage, correlationId);
        }).exceptionally(e -> {
            int status;
            if(e instanceof JwtAuthException){
                status = 401;
            } else {
                status = 500;
            }
            MessageTemplate<UserAuthResponse> responseData = new MessageTemplate<>(status, e.getMessage());
            sendResponse(responseData, correlationId);
            return null;
        });
    }

    private void sendResponse(MessageTemplate<UserAuthResponse> responseMessage, String correlationId) {
        log.info("Sending response back");
        ProducerRecord<String, MessageTemplate<UserAuthResponse>> response =
                new ProducerRecord<>("auth-topic-reply", correlationId, responseMessage);
        kafkaUserResponseSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(data -> log.info("Response sent successfully"))
                .doOnError(e -> log.error("Error sending response: ", e))
                .subscribe();
    }
}
