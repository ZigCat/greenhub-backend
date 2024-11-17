package com.github.zigcat.greenhub.api_gateway.kafka.config;

import com.github.zigcat.greenhub.api_gateway.gateway.dto.JwtRequest;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.UserResponse;
import com.github.zigcat.greenhub.api_gateway.kafka.dto.KafkaMessageTemplate;
import com.github.zigcat.greenhub.api_gateway.kafka.jackson.JsonDeserializer;
import com.github.zigcat.greenhub.api_gateway.kafka.jackson.JsonSerializer;
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
    public ProducerFactory<String, KafkaMessageTemplate<JwtRequest>> producerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        return new DefaultKafkaProducerFactory<>(props,
                new StringSerializer(),
                new JsonSerializer<>());
    }

    @Bean
    public ConsumerFactory<String, KafkaMessageTemplate<UserResponse>> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-auth");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(UserResponse.class));
    }

    @Bean
    public KafkaMessageListenerContainer<String, KafkaMessageTemplate<UserResponse>> container(
            ConsumerFactory<String, KafkaMessageTemplate<UserResponse>> consumerFactory
    ){
        ContainerProperties properties = new ContainerProperties
                (Pattern.compile("auth-reply-topic-.*"));
        return new KafkaMessageListenerContainer<>(consumerFactory, properties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, KafkaMessageTemplate<JwtRequest>, KafkaMessageTemplate<UserResponse>> replyingTemplate(
            ProducerFactory<String, KafkaMessageTemplate<JwtRequest>> producerFactory,
            KafkaMessageListenerContainer<String, KafkaMessageTemplate<UserResponse>> container
    ){
        return new ReplyingKafkaTemplate<>(producerFactory, container);
    }
}
