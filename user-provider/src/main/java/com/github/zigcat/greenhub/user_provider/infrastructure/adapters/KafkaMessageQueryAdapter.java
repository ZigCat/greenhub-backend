package com.github.zigcat.greenhub.user_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.user_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.user_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.MessageQueryAdapter;
import com.github.zigcat.greenhub.user_provider.domain.MessageTemplate;
import com.github.zigcat.greenhub.user_provider.application.events.AuthorizeEvent;
import com.github.zigcat.greenhub.user_provider.application.events.LoginEvent;
import com.github.zigcat.greenhub.user_provider.application.events.RegisterEvent;
import com.github.zigcat.greenhub.user_provider.infrastructure.InfrastructureDTO;
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
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private final KafkaSender<String, MessageTemplate<AppUser>> userSender;
    private final KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserRegister>> registerReceiver;
    private final KafkaReceiver<String, MessageTemplate<String>> authReceiver;
    private final KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserLogin>> loginReceiver;
    private final ApplicationEventPublisher applicationEventPublisher;

    public KafkaMessageQueryAdapter(
            KafkaSender<String, MessageTemplate<AppUser>> userSender,
            KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserRegister>> registerReceiver,
            KafkaReceiver<String, MessageTemplate<String>> authReceiver,
            KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserLogin>> loginReceiver,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.userSender = userSender;
        this.registerReceiver = registerReceiver;
        this.authReceiver = authReceiver;
        this.loginReceiver = loginReceiver;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void processRegisterMessage() {
        registerReceiver.receive()
                .bufferTimeout(10, Duration.ofMillis(500))
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .doOnNext(batch -> {
                    log.info("REGISTER: Processing batch of {} messages", batch.size());
                    batch.forEach(record -> {
                        String correlationId = record.key();
                        log.info("REGISTER: Processing message with id {}", correlationId);
                        InfrastructureDTO.UserRegister data = record.value().getPayload();
                        processRegistration(data, correlationId);
                    });
                })
                .doOnError(e -> log.error("Error while receiving message ", e))
                .subscribe();
    }

    private void processRegistration(InfrastructureDTO.UserRegister dto, String correlationId){
        log.info("Preparing event...");
        CompletableFuture<AppUser> replyFuture =
                new CompletableFuture<>();
        RegisterEvent event =
                new RegisterEvent(this, dto, replyFuture);
        log.info("Publishing event...");
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            MessageTemplate<AppUser> responseMessage = new MessageTemplate<>(reply);
            sendRegisterResponse(responseMessage, correlationId);
        });
    }

    private void sendRegisterResponse(MessageTemplate<AppUser> responseMessage, String correlationId){
        log.info("Preparing response message...");
        ProducerRecord<String, MessageTemplate<AppUser>> response =
                new ProducerRecord<>("user-reg-topic-reply", correlationId, responseMessage);
        userSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(res -> log.info("Message sent successfully"))
                .doOnError(e -> log.error("Error while sending response ", e))
                .subscribe();
    }

    @Override
    public void processAuthorizeMessage() {
        authReceiver.receive()
                .bufferTimeout(10, Duration.ofMillis(500))
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .doOnNext(batch -> {
                    log.info("AUTH: Processing batch of {} messages", batch.size());
                    batch.forEach(record -> {
                        String correlationId = record.key();
                        log.info("AUTH: Processing message with id {}", correlationId);
                        String data = record.value().getPayload();
                        processAuthorization(data, correlationId);
                    });
                })
                .doOnError(e -> log.error("Error while receiving message ", e))
                .subscribe();
    }

    private void processAuthorization(String data, String correlationId){
        CompletableFuture<AppUser> replyFuture =
                new CompletableFuture<>();
        AuthorizeEvent event =
                new AuthorizeEvent(this, data, replyFuture);
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            MessageTemplate<AppUser> responseMessage = new MessageTemplate<>(reply);
            sendAuthorizeResponse(responseMessage, correlationId);
        }).exceptionally(e -> {
            int status;
            if(e instanceof NotFoundAppException){
                status = 404;
            } else {
                status = 500;
            }
            MessageTemplate<AppUser> responseMessage = new MessageTemplate<>(status, e.getMessage());
            sendAuthorizeResponse(responseMessage, correlationId);
            return null;
        });
    }

    private void sendAuthorizeResponse(MessageTemplate<AppUser> responseMessage, String correlationId){
        ProducerRecord<String, MessageTemplate<AppUser>> response =
                new ProducerRecord<>("user-auth-topic-reply", correlationId, responseMessage);
        userSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(res -> log.info("Message sent successfully"))
                .doOnError(e -> log.error("Error while sending response ", e))
                .subscribe();
    }

    @Override
    public void processLoginMessage() {
        loginReceiver.receive()
                .bufferTimeout(10, Duration.ofMillis(500))
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .doOnNext(batch -> {
                    log.info("LOGIN: Processing batch of {} messages", batch.size());
                    batch.forEach(record -> {
                        String correlationId = record.key();
                        log.info("LOGIN: Processing message with id {}", correlationId);
                        InfrastructureDTO.UserLogin data = record.value().getPayload();
                        processLogin(data, correlationId);
                    });
                })
                .doOnError(e -> log.error("Error while receiving message ", e))
                .subscribe();
    }

    private void processLogin(InfrastructureDTO.UserLogin dto, String correlationId){
        CompletableFuture<AppUser> replyFuture = new CompletableFuture<>();
        LoginEvent event = new LoginEvent(this, dto, replyFuture);
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            MessageTemplate<AppUser> responseMessage = new MessageTemplate<>(reply);
            sendLoginResponse(responseMessage, correlationId);
        }).exceptionally(e -> {
            int status;
            if(e instanceof ForbiddenAppException){
                status = 403;
            } else if(e instanceof NotFoundAppException){
                status = 404;
            } else {
                status = 500;
            }
            MessageTemplate<AppUser> responseData = new MessageTemplate<>(status, e.getMessage());
            sendAuthorizeResponse(responseData, correlationId);
            return null;
        });
    }

    private void sendLoginResponse(MessageTemplate<AppUser> responseMessage, String correlationId){
        ProducerRecord<String, MessageTemplate<AppUser>> response =
                new ProducerRecord<>("user-login-topic-reply", correlationId, responseMessage);
        userSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(res -> log.info("Message sent successfully"))
                .doOnError(e -> log.error("Error while sending response ", e))
                .subscribe();
    }
}
