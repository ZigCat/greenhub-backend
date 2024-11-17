package com.github.zigcat.greenhub.auth_provider.kafka.config;

import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOResponsible;
import com.github.zigcat.greenhub.auth_provider.dto.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.dto.responses.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.kafka.dto.KafkaMessageTemplate;
import com.github.zigcat.greenhub.auth_provider.kafka.jackson.JsonDeserializer;
import com.github.zigcat.greenhub.auth_provider.kafka.jackson.JsonSerializer;
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
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        return new KafkaAdmin(config);
    }

    @Bean
    public AdminClient adminClient(){
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        return AdminClient.create(config);
    }

    @Bean
    public NewTopic authUserTopic(){
        return new NewTopic("auth-user-topic", 10, (short) 1);
    }

    @Bean
    public NewTopic regTopic(){
        return new NewTopic("reg-topic", 10, (short) 1);
    }

    @Bean
    public ProducerFactory<String, KafkaMessageTemplate<UserAuthResponse>> producerFactory(){
        System.out.println(BOOTSTRAP_SERVER);
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        return new DefaultKafkaProducerFactory<>(props,
                new StringSerializer(),
                new JsonSerializer<>());
    }

    @Bean
    public ConsumerFactory<String, KafkaMessageTemplate<JwtRequest>> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-auth");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(JwtRequest.class));
    }

    @Bean
    public KafkaTemplate<String, KafkaMessageTemplate<UserAuthResponse>> kafkaTemplate(
            ProducerFactory<String, KafkaMessageTemplate<UserAuthResponse>> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }
}
