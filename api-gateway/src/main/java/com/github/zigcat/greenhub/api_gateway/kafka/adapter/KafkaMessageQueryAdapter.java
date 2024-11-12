package com.github.zigcat.greenhub.api_gateway.kafka.adapter;

import com.github.zigcat.greenhub.api_gateway.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTOResponsible;
import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import com.github.zigcat.greenhub.api_gateway.exceptions.ServerException;
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
    private final ReplyingKafkaTemplate<String, KafkaMessageTemplate<DTORequestible>, KafkaMessageTemplate<DTOResponsible>> replyingTemplate;
    private final AdminClient adminClient;

    @Autowired
    public KafkaMessageQueryAdapter(ReplyingKafkaTemplate<String, KafkaMessageTemplate<DTORequestible>, KafkaMessageTemplate<DTOResponsible>> replyingTemplate,
                                    AdminClient adminClient) {
        this.replyingTemplate = replyingTemplate;
        this.adminClient = adminClient;
    }

    @Override
    public DTOResponsible performAndAwait(String requestTopic, String replyTopic, DTORequestible data) {
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
