package com.github.zigcat.greenhub.api_gateway.kafka.config;

import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTOResponsible;
import com.github.zigcat.greenhub.api_gateway.kafka.jackson.JsonDeserializer;
import com.github.zigcat.greenhub.api_gateway.kafka.jackson.JsonSerializer;
import com.github.zigcat.greenhub.api_gateway.kafka.dto.KafkaMessageTemplate;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class KafkaConfiguration {
    @Value("${kafka.bootstrap-server}")
    private String BOOTSTRAP_SERVER;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        return new KafkaAdmin(props);
    }

    @Bean
    public AdminClient adminClient(){
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        return AdminClient.create(config);
    }

    @Bean
    public NewTopic authTopic(){
        return new NewTopic("auth-topic", 10, (short) 1);
    }

    @Bean
    public ProducerFactory<String, KafkaMessageTemplate<DTORequestible>> producerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, KafkaMessageTemplate<DTOResponsible>> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-auth");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public KafkaMessageListenerContainer<String, KafkaMessageTemplate<DTOResponsible>> container(
            ConsumerFactory<String, KafkaMessageTemplate<DTOResponsible>> consumerFactory
    ){
        ContainerProperties properties = new ContainerProperties
                (Pattern.compile("auth-reply-topic-.*"));
        return new KafkaMessageListenerContainer<>(consumerFactory, properties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, KafkaMessageTemplate<DTORequestible>, KafkaMessageTemplate<DTOResponsible>> replyingTemplate(
            ProducerFactory<String, KafkaMessageTemplate<DTORequestible>> producerFactory,
            KafkaMessageListenerContainer<String, KafkaMessageTemplate<DTOResponsible>> container
    ){
        return new ReplyingKafkaTemplate<>(producerFactory, container);
    }
}
