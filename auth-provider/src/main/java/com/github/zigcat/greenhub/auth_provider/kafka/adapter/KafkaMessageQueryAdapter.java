package com.github.zigcat.greenhub.auth_provider.kafka.adapter;

import com.github.zigcat.greenhub.auth_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOInstance;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOResponsible;
import com.github.zigcat.greenhub.auth_provider.dto.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.dto.responses.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.events.replies.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.auth_provider.events.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.auth_provider.exceptions.AuthException;
import com.github.zigcat.greenhub.auth_provider.exceptions.ServerException;
import com.github.zigcat.greenhub.auth_provider.kafka.dto.KafkaMessageTemplate;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private final KafkaTemplate<String, KafkaMessageTemplate<UserAuthResponse>> kafkaTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    public KafkaMessageQueryAdapter(
            KafkaTemplate<String, KafkaMessageTemplate<UserAuthResponse>> kafkaTemplate,
            ApplicationEventPublisher applicationEventPublisher) {
        this.kafkaTemplate = kafkaTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @KafkaListener(topics = "auth-topic", groupId = "greenhub-auth")
    public void listen(ConsumerRecord<String, KafkaMessageTemplate<JwtRequest>> record,
                       @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic) {
        KafkaMessageTemplate<JwtRequest> requestMessage = record.value();
        JwtRequest request = requestMessage.getPayload();
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture = new CompletableFuture<>();
        AuthorizeMessageQueryAdapterEvent event =
                new AuthorizeMessageQueryAdapterEvent(this, request, replyFuture);
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            UserAuthResponse userResponse = reply.getUserResponse();
            KafkaMessageTemplate<UserAuthResponse> response = new KafkaMessageTemplate<>(userResponse);
            kafkaTemplate.send(replyTopic, response);
        }).exceptionally(e -> {
            KafkaMessageTemplate<UserAuthResponse> errorResponse = new KafkaMessageTemplate<>(500, e.getMessage());
            kafkaTemplate.send(replyTopic, errorResponse);
            return null;
        });
    }
}
