package com.github.zigcat.greenhub.user_provider.kafka.adapter;

import com.github.zigcat.greenhub.user_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.user_provider.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.user_provider.dto.requests.UserAuthRequest;
import com.github.zigcat.greenhub.user_provider.dto.requests.UserRegisterRequest;
import com.github.zigcat.greenhub.user_provider.events.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.events.RegisterAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.RegisterMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.kafka.dto.KafkaMessageTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<String, KafkaMessageTemplate<DTORequestible>> kafkaTemplate;

    @Autowired
    public KafkaMessageQueryAdapter(ApplicationEventPublisher applicationEventPublisher,
                                    KafkaTemplate<String, KafkaMessageTemplate<DTORequestible>> kafkaTemplate) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "auth-user-topic", groupId = "greenhub-auth")
    public void listenAuth(ConsumerRecord<String, KafkaMessageTemplate<DTORequestible>> record,
                       @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic) {
        UserAuthRequest request = (UserAuthRequest) record.value().getPayload();
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture = new CompletableFuture<>();
        AuthorizeMessageQueryAdapterEvent event =
                new AuthorizeMessageQueryAdapterEvent(this, request, replyFuture);
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            KafkaMessageTemplate<DTORequestible> response =
                    new KafkaMessageTemplate<>(reply.getResponse());
            kafkaTemplate.send(replyTopic, response);
        }).exceptionally(e -> {
            KafkaMessageTemplate<DTORequestible> response =
                    new KafkaMessageTemplate<>(500, e.getMessage());
            kafkaTemplate.send(replyTopic, response);
            return null;
        });
    }

    @KafkaListener(topics = "reg-topic", groupId = "greenhub-auth")
    public void listenReg(ConsumerRecord<String, KafkaMessageTemplate<DTORequestible>> record,
                       @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic){
        UserRegisterRequest request = (UserRegisterRequest) record.value().getPayload();
        CompletableFuture<RegisterAuthServiceReply> replyFuture = new CompletableFuture<>();
        RegisterMessageQueryAdapterEvent event =
                new RegisterMessageQueryAdapterEvent(this, request, replyFuture);
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            KafkaMessageTemplate<DTORequestible> response =
                    new KafkaMessageTemplate<>(reply.getResponse());
            kafkaTemplate.send(replyTopic, response);
        }).exceptionally(e -> {
            KafkaMessageTemplate<DTORequestible> response =
                    new KafkaMessageTemplate<>(500, e.getMessage());
            kafkaTemplate.send(replyTopic, response);
            return null;
        });
    }
}
