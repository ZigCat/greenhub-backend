package com.github.zigcat.greenhub.api_gateway.kafka.adapter;

import com.github.zigcat.greenhub.api_gateway.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import com.github.zigcat.greenhub.api_gateway.exceptions.ServerException;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.JwtRequest;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.UserResponse;
import com.github.zigcat.greenhub.api_gateway.kafka.dto.KafkaMessageTemplate;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private final ReplyingKafkaTemplate<String, KafkaMessageTemplate<JwtRequest>, KafkaMessageTemplate<UserResponse>> replyingTemplate;
    private final AdminClient adminClient;

    @Autowired
    public KafkaMessageQueryAdapter(
            ReplyingKafkaTemplate<
                    String,
                    KafkaMessageTemplate<JwtRequest>,
                    KafkaMessageTemplate<UserResponse>
                    > replyingTemplate,
            AdminClient adminClient
    ) {
        this.replyingTemplate = replyingTemplate;
        this.adminClient = adminClient;
    }

    @Override
    public UserResponse performAndAwait(String requestTopic, String replyTopic, JwtRequest data) {
        String uniqueId = UUID.randomUUID().toString();
        KafkaMessageTemplate<JwtRequest> request = new KafkaMessageTemplate<>(data);
        replyTopic = replyTopic + uniqueId;
        createTopic(replyTopic);
        ProducerRecord<String, KafkaMessageTemplate<JwtRequest>> record = new ProducerRecord<>(requestTopic, request);
        record.headers().add(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes());
        RequestReplyFuture<String, KafkaMessageTemplate<JwtRequest>, KafkaMessageTemplate<UserResponse>> replyFuture =
                replyingTemplate.sendAndReceive(record);
        KafkaMessageTemplate<UserResponse> response;
        try {
            response = replyFuture.get().value();
            if(response.getStatus() == 500){
                throw new ServerException(response.getMessage());
            } else if(response.getStatus() == 401 || response.getStatus() == 403){
                throw new AuthException(response.getMessage());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException(e.getMessage());
        } finally {
            deleteTopic(replyTopic);
        }
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
