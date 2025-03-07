package com.github.zigcat.greenhub.api_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.api_gateway.domain.MessageTemplate;
import com.github.zigcat.greenhub.api_gateway.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.api_gateway.infrastructure.jackson.MessageTemplateDeserializer;
import com.github.zigcat.greenhub.api_gateway.infrastructure.jackson.MessageTemplateSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.*;

@Configuration
public class KafkaConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVER;
    private ObjectMapper objectMapper;

    @Autowired
    public KafkaConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public NewTopic authTopic(){
        return new NewTopic("auth-topic", 10, (short) 1);
    }

    @Bean
    public NewTopic authTopicReply(){
        return new NewTopic("auth-topic-reply", 10, (short) 1);
    }

    @Bean
    public NewTopic regTopic(){
        return new NewTopic("user-reg-topic", 10, (short) 1);
    }

    @Bean
    public NewTopic regTopicReply(){
        return new NewTopic("user-reg-topic-reply", 10, (short) 1);
    }

    @Bean
    public KafkaSender<String, MessageTemplate<InfrastructureDTO.JwtDTO>> jwtSender(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageTemplateSerializer.class);
        props.put("custom.object.mapper", objectMapper);
        return KafkaSender.create(SenderOptions.create(props));
    }

    @Bean
    public KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserAuth>> userAuthReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-auth-reply");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageTemplateDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("value.deserializer.type", InfrastructureDTO.UserAuth.class.getName());
        props.put("custom.object.mapper", objectMapper);
        ReceiverOptions<String, MessageTemplate<InfrastructureDTO.UserAuth>> receiverOptions =
                ReceiverOptions.create(props);
        receiverOptions = receiverOptions.subscription(Collections.singleton("auth-topic-reply"));
        return KafkaReceiver.create(receiverOptions);
    }
}
