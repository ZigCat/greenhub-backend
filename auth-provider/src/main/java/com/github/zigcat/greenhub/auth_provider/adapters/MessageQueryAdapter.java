package com.github.zigcat.greenhub.auth_provider.adapters;

import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOResponsible;
import com.github.zigcat.greenhub.auth_provider.dto.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.kafka.dto.KafkaMessageTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface MessageQueryAdapter {
    DTOResponsible sendAndAwait(String requestTopic, String replyTopic, DTORequestible data);
    void createTopic(String topicName);
    void deleteTopic(String topicName);
}
