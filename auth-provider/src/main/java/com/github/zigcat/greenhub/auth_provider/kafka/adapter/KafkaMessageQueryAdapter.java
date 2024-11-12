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
    private final KafkaTemplate<String, KafkaMessageTemplate<DTORequestible>> kafkaTemplate;
    private final ReplyingKafkaTemplate<String, KafkaMessageTemplate<DTORequestible>, KafkaMessageTemplate<DTOResponsible>> replyingTemplate;
    private final AdminClient adminClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    public KafkaMessageQueryAdapter(
            KafkaTemplate<String, KafkaMessageTemplate<DTORequestible>> kafkaTemplate,
            ReplyingKafkaTemplate<String, KafkaMessageTemplate<DTORequestible>, KafkaMessageTemplate<DTOResponsible>> replyingTemplate,
            AdminClient adminClient,
            ApplicationEventPublisher applicationEventPublisher) {
        this.kafkaTemplate = kafkaTemplate;
        this.replyingTemplate = replyingTemplate;
        this.adminClient = adminClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @KafkaListener(topics = "auth-topic", groupId = "greenhub-auth")
    public void listen(ConsumerRecord<String, KafkaMessageTemplate<DTOInstance>> record,
                       @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic) {
        KafkaMessageTemplate<DTOInstance> requestMessage = record.value();
        DTOResponsible request = (DTOResponsible) requestMessage.getPayload();
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture = new CompletableFuture<>();
        AuthorizeMessageQueryAdapterEvent event =
                new AuthorizeMessageQueryAdapterEvent(this, (JwtRequest) request, replyFuture);
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            UserAuthResponse userResponse = reply.getUserResponse();
            KafkaMessageTemplate<DTORequestible> response = new KafkaMessageTemplate<>((DTORequestible) userResponse);
            kafkaTemplate.send(replyTopic, response);
        }).exceptionally(e -> {
            KafkaMessageTemplate<DTORequestible> errorResponse = new KafkaMessageTemplate<>(500, e.getMessage());
            kafkaTemplate.send(replyTopic, errorResponse);
            return null;
        });
    }

    @Override
    public DTOResponsible sendAndAwait(String requestTopic, String replyTopic, DTORequestible data) {
        String uniqueId = UUID.randomUUID().toString();
        KafkaMessageTemplate<DTORequestible> request = new KafkaMessageTemplate<>(data);
        replyTopic = replyTopic + uniqueId;
        createTopic(replyTopic);
        ProducerRecord<String, KafkaMessageTemplate<DTORequestible>> record = new ProducerRecord<>(requestTopic, request);
        record.headers().add(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes());
        RequestReplyFuture<String, KafkaMessageTemplate<DTORequestible>, KafkaMessageTemplate<DTOResponsible>> replyFuture =
                replyingTemplate.sendAndReceive(record);
        KafkaMessageTemplate<DTOResponsible> response;
        try {
            response = replyFuture.get().value();
            if(response.getStatus() == 500){
                throw new ServerException(response.getMessage());
            } else if(response.getStatus() == 401 || response.getStatus() == 403){
                throw new AuthException(response.getMessage());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException(e.getMessage());
        }
        deleteTopic(replyTopic);
        return response.getPayload();
    }

    @Override
    public void createTopic(String topicName) {
        NewTopic topic = new NewTopic(topicName, 1, (short) 1);
        adminClient.createTopics(List.of(topic));
    }

    @Override
    public void deleteTopic(String topicName) {
        adminClient.deleteTopics(List.of(topicName));
    }
}
