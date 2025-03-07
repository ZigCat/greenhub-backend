package com.github.zigcat.greenhub.auth_provider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.infrastructure.adapter.dto.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.infrastructure.adapter.dto.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.domain.MessageTemplate;
import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.auth_provider.infrastructure.jackson.MessageTemplateDeserializer;
import com.github.zigcat.greenhub.auth_provider.infrastructure.jackson.MessageTemplateSerializer;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public KafkaSender<String, MessageTemplate<InfrastructureDTO.UserRegister>> registerSender(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageTemplateSerializer.class);
        props.put("custom.object.mapper", objectMapper);
        return KafkaSender.create(SenderOptions.create(props));
    }

    @Bean
    public KafkaSender<String, MessageTemplate<String>> authSender(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageTemplateSerializer.class);
        props.put("custom.object.mapper", objectMapper);
        return KafkaSender.create(SenderOptions.create(props));
    }

    @Bean
    public KafkaReceiver<String, MessageTemplate<AppUser>> userRegisterReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-user-reg-reply");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageTemplateDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("value.deserializer.type", AppUser.class.getName());
        props.put("custom.object.mapper", objectMapper);
        ReceiverOptions<String, MessageTemplate<AppUser>> receiverOptions =
                ReceiverOptions.create(props);
        receiverOptions = receiverOptions.subscription(Collections.singleton("user-reg-topic-reply"));
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaReceiver<String, MessageTemplate<AppUser>> userAuthReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-user-auth-reply");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageTemplateDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("value.deserializer.type", AppUser.class.getName());
        props.put("custom.object.mapper", objectMapper);
        ReceiverOptions<String, MessageTemplate<AppUser>> receiverOptions =
                ReceiverOptions.create(props);
        receiverOptions = receiverOptions.subscription(Collections.singleton("user-auth-topic-reply"));
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaSender<String, MessageTemplate<InfrastructureDTO.UserLogin>> loginSender(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageTemplateSerializer.class);
        props.put("custom.object.mapper", objectMapper);
        return KafkaSender.create(SenderOptions.create(props));
    }

    @Bean
    public KafkaReceiver<String, MessageTemplate<AppUser>> userLoginReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-user-login-reply");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageTemplateDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("value.deserializer.type", AppUser.class.getName());
        props.put("custom.object.mapper", objectMapper);
        ReceiverOptions<String, MessageTemplate<AppUser>> receiverOptions =
                ReceiverOptions.create(props);
        receiverOptions = receiverOptions.subscription(Collections.singleton("user-login-topic-reply"));
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaReceiver<String, MessageTemplate<JwtRequest>> jwtReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-auth");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageTemplateDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put("value.deserializer.type", JwtRequest.class.getName());
        props.put("custom.object.mapper", objectMapper);
        ReceiverOptions<String, MessageTemplate<JwtRequest>> receiverOptions =
                ReceiverOptions.create(props);
        receiverOptions = receiverOptions.subscription(Collections.singleton("auth-topic"));
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaSender<String, MessageTemplate<UserAuthResponse>> userGatewaySender(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageTemplateSerializer.class);
        props.put("custom.object.mapper", objectMapper);
        return KafkaSender.create(SenderOptions.create(props));
    }
}
