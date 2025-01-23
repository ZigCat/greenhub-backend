package com.github.zigcat.greenhub.user_provider.kafka.adapter;

import com.github.zigcat.greenhub.user_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.user_provider.dto.mq.requests.AuthorizeRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.requests.LoginRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.UserAuthResponse;
import com.github.zigcat.greenhub.user_provider.dto.mq.template.MessageTemplate;
import com.github.zigcat.greenhub.user_provider.events.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.events.events.LoginMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.events.replies.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.replies.RegisterAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.events.RegisterMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.exceptions.AuthException;
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

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private final KafkaSender<String, MessageTemplate<RegisterResponse>> kafkaRegisterResponseSender;
    private final KafkaSender<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseSender;
    private final KafkaReceiver<String, MessageTemplate<RegisterRequest>> kafkaRegisterRequestReceiver;
    private final KafkaReceiver<String, MessageTemplate<AuthorizeRequest>> kafkaAuthorizeRequestReceiver;
    private final KafkaReceiver<String, MessageTemplate<LoginRequest>> kafkaLoginRequestReceiver;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public KafkaMessageQueryAdapter(
            KafkaSender<String, MessageTemplate<RegisterResponse>> kafkaRegisterResponseSender,
            KafkaSender<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseSender,
            KafkaReceiver<String, MessageTemplate<RegisterRequest>> kafkaRegisterRequestReceiver,
            KafkaReceiver<String, MessageTemplate<AuthorizeRequest>> kafkaAuthorizeRequestReceiver,
            KafkaReceiver<String, MessageTemplate<LoginRequest>> kafkaLoginRequestReceiver,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.kafkaRegisterResponseSender = kafkaRegisterResponseSender;
        this.kafkaUserResponseSender = kafkaUserResponseSender;
        this.kafkaRegisterRequestReceiver = kafkaRegisterRequestReceiver;
        this.kafkaAuthorizeRequestReceiver = kafkaAuthorizeRequestReceiver;
        this.kafkaLoginRequestReceiver = kafkaLoginRequestReceiver;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void processRegisterMessage() {
        kafkaRegisterRequestReceiver.receive()
                .doOnNext(record -> {
                    String correlationId = record.key();
                    log.info("Received message with id {}", correlationId);
                    RegisterRequest requestData = record.value().getPayload();
                    processRegistration(requestData, correlationId);
                })
                .doOnError(e -> log.error("Error while receiving message ", e))
                .subscribe();
    }

    @Override
    public void processAuthorizeMessage() {
        kafkaAuthorizeRequestReceiver.receive()
                .doOnNext(record -> {
                    String correlationId = record.key();
                    AuthorizeRequest requestData = record.value().getPayload();
                    processAuthorization(requestData, correlationId);
                })
                .doOnError(e -> log.error("Error while receiving message ", e))
                .subscribe();
    }

    @Override
    public void processLoginMessage() {
        kafkaLoginRequestReceiver.receive()
                .doOnNext(record -> {
                    String correlationId = record.key();
                    LoginRequest requestData = record.value().getPayload();
                    processLogin(requestData, correlationId);
                })
                .doOnError(e -> log.error("Error while receiving message ", e))
                .subscribe();
    }

    private void processRegistration(RegisterRequest requestData, String correlationId){
        log.info("Preparing event...");
        CompletableFuture<RegisterAuthServiceReply> replyFuture =
                new CompletableFuture<>();
        RegisterMessageQueryAdapterEvent event =
                new RegisterMessageQueryAdapterEvent(this, requestData, replyFuture);
        log.info("Publishing event...");
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            MessageTemplate<RegisterResponse> response = new MessageTemplate<>(reply.getResponse());
            sendRegisterResponse(response, correlationId);
        });
    }

    private void sendRegisterResponse(MessageTemplate<RegisterResponse> responseMessage, String correlationId){
        log.info("Preparing response message...");
        ProducerRecord<String, MessageTemplate<RegisterResponse>> response =
                new ProducerRecord<>("user-reg-topic-reply", correlationId, responseMessage);
        kafkaRegisterResponseSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(res -> log.info("Message sent successfully"))
                .doOnError(e -> log.error("Error while sending response ", e))
                .subscribe();
    }

    private void processAuthorization(AuthorizeRequest requestData, String correlationId){
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture =
                new CompletableFuture<>();
        AuthorizeMessageQueryAdapterEvent event =
                new AuthorizeMessageQueryAdapterEvent(this, requestData, replyFuture);
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            MessageTemplate<UserAuthResponse> responseData = new MessageTemplate<>(reply.getResponse());
            sendAuthorizeResponse(responseData, correlationId);
        }).exceptionally(e -> {
            int status;
            if(e instanceof NotFoundException){
                status = 404;
            } else {
                status = 500;
            }
            MessageTemplate<UserAuthResponse> responseData = new MessageTemplate<>(status, e.getMessage());
            sendAuthorizeResponse(responseData, correlationId);
            return null;
        });
    }

    private void sendAuthorizeResponse(MessageTemplate<UserAuthResponse> responseMessage, String correlationId){
        ProducerRecord<String, MessageTemplate<UserAuthResponse>> response =
                new ProducerRecord<>("user-auth-topic-reply", correlationId, responseMessage);
        kafkaUserResponseSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(res -> log.info("Message sent successfully"))
                .doOnError(e -> log.error("Error while sending response ", e))
                .subscribe();
    }

    private void processLogin(LoginRequest requestData, String correlationId){
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture =
                new CompletableFuture<>();
        LoginMessageQueryAdapterEvent event =
                new LoginMessageQueryAdapterEvent(this, requestData, replyFuture);
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            MessageTemplate<UserAuthResponse> responseMessage = new MessageTemplate<>(reply.getResponse());
            sendLoginResponse(responseMessage, correlationId);
        }).exceptionally(e -> {
            int status;
            if(e instanceof AuthException){
                status = 403;
            } else if(e instanceof NotFoundException){
                status = 404;
            } else {
                status = 500;
            }
            MessageTemplate<UserAuthResponse> responseData = new MessageTemplate<>(status, e.getMessage());
            sendAuthorizeResponse(responseData, correlationId);
            return null;
        });
    }

    private void sendLoginResponse(MessageTemplate<UserAuthResponse> responseMessage, String correlationId){
        ProducerRecord<String, MessageTemplate<UserAuthResponse>> response =
                new ProducerRecord<>("user-login-topic-reply", correlationId, responseMessage);
        kafkaUserResponseSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(res -> log.info("Message sent successfully"))
                .doOnError(e -> log.error("Error while sending response ", e))
                .subscribe();
    }
}
