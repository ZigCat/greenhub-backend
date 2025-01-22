package com.github.zigcat.greenhub.auth_provider.kafka.adapter;

import com.github.zigcat.greenhub.auth_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.auth_provider.events.replies.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.auth_provider.events.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.auth_provider.dto.mq.template.MessageTemplate;
import com.github.zigcat.greenhub.auth_provider.exceptions.ServerException;
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
    private ApplicationEventPublisher applicationEventPublisher;
    private KafkaReceiver<String, MessageTemplate<JwtRequest>> kafkaJwtRequestReceiver;
    private KafkaReceiver<String, MessageTemplate<RegisterResponse>> kafkaRegisterResponseReceiver;
    private KafkaSender<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseSender;
    private KafkaSender<String, MessageTemplate<RegisterRequest>> kafkaRegisterRequestSender;

    @Autowired
    public KafkaMessageQueryAdapter(
            ApplicationEventPublisher applicationEventPublisher,
            KafkaReceiver<String, MessageTemplate<JwtRequest>> kafkaJwtRequestReceiver,
            KafkaReceiver<String, MessageTemplate<RegisterResponse>> kafkaRegisterResponseReceiver,
            KafkaSender<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseSender,
            KafkaSender<String, MessageTemplate<RegisterRequest>> kafkaRegisterRequestSender
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.kafkaJwtRequestReceiver = kafkaJwtRequestReceiver;
        this.kafkaRegisterResponseReceiver = kafkaRegisterResponseReceiver;
        this.kafkaUserResponseSender = kafkaUserResponseSender;
        this.kafkaRegisterRequestSender = kafkaRegisterRequestSender;
    }

    @Override
    public void processMessage() {
        kafkaJwtRequestReceiver.receive()
                .doOnNext(record -> {
                    String correlationId = record.key();
                    log.info("Received message with id {}", correlationId);
                    JwtRequest requestData = record.value().getPayload();
                    log.info("Received token {}", requestData.getToken());
                    processRequest(requestData, correlationId);
                })
                .doOnError(e -> log.error("Error while receiving message", e))
                .subscribe();
    }

    @Override
    public Mono<RegisterResponse> registerAndAwait(RegisterRequest data) {
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<RegisterRequest> requestData = new MessageTemplate<>(data);
        ProducerRecord<String, MessageTemplate<RegisterRequest>> request =
                new ProducerRecord<>("user-reg-topic", correlationId, requestData);
        return kafkaRegisterRequestSender.send(Mono.just(SenderRecord.create(request, correlationId)))
                .then(kafkaRegisterResponseReceiver.receive()
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
                        .next());
    }

    private void processRequest(JwtRequest requestData, String correlationId) {
        log.info("Preparing event for AuthService...");
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture =
                new CompletableFuture<>();
        AuthorizeMessageQueryAdapterEvent event =
                new AuthorizeMessageQueryAdapterEvent(this, requestData, replyFuture);
        log.info("Publishing event...");
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            log.info("Reply accepted, preparing response...");
            UserAuthResponse response = reply.getUserResponse();
            MessageTemplate<UserAuthResponse> responseMessage = new MessageTemplate<>(response);
            sendResponse(responseMessage, correlationId);
        });
    }

    private void sendResponse(MessageTemplate<UserAuthResponse> responseMessage, String correlationId) {
        log.info("Sending response back...");
        ProducerRecord<String, MessageTemplate<UserAuthResponse>> response =
                new ProducerRecord<>("auth-topic-reply", correlationId, responseMessage);
        kafkaUserResponseSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(data -> log.info("Response sent successfully"))
                .doOnError(e -> log.error("Error sending response: ", e))
                .subscribe();
    }
}
