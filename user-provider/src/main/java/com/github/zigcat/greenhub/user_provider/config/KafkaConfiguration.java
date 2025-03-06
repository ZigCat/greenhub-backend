package com.github.zigcat.greenhub.user_provider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.domain.MessageTemplate;
import com.github.zigcat.greenhub.user_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.user_provider.infrastructure.jackson.MessageTemplateDeserializer;
import com.github.zigcat.greenhub.user_provider.infrastructure.jackson.MessageTemplateSerializer;
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
    public KafkaSender<String, MessageTemplate<AppUser>> userSender(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageTemplateSerializer.class);
        props.put("custom.object.mapper", objectMapper);
        return KafkaSender.create(SenderOptions.create(props));
    }

    @Bean
    public KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserRegister>> registerReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-user-reg");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageTemplateDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put("value.deserializer.type", InfrastructureDTO.UserRegister.class.getName());
        props.put("custom.object.mapper", objectMapper);
        ReceiverOptions<String, MessageTemplate<InfrastructureDTO.UserRegister>> receiverOptions =
                ReceiverOptions.create(props);
        receiverOptions = receiverOptions.subscription(Collections.singleton("user-reg-topic"));
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaReceiver<String, MessageTemplate<String>> authReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-user-auth");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageTemplateDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put("value.deserializer.type", String.class.getName());
        props.put("custom.object.mapper", objectMapper);
        ReceiverOptions<String, MessageTemplate<String>> receiverOptions =
                ReceiverOptions.create(props);
        receiverOptions = receiverOptions.subscription(Collections.singleton("user-auth-topic"));
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaReceiver<String, MessageTemplate<InfrastructureDTO.UserLogin>> loginReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greenhub-user-login");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageTemplateDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put("value.deserializer.type", InfrastructureDTO.UserLogin.class.getName());
        props.put("custom.object.mapper", objectMapper);
        ReceiverOptions<String, MessageTemplate<InfrastructureDTO.UserLogin>> receiverOptions =
                ReceiverOptions.create(props);
        receiverOptions = receiverOptions.subscription(Collections.singleton("user-login-topic"));
        return KafkaReceiver.create(receiverOptions);
    }
}
