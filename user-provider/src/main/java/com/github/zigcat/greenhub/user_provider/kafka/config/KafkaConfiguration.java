package com.github.zigcat.greenhub.user_provider.kafka.config;

import com.github.zigcat.greenhub.user_provider.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.user_provider.dto.datatypes.DTOResponsible;
import com.github.zigcat.greenhub.user_provider.dto.requests.UserAuthRequest;
import com.github.zigcat.greenhub.user_provider.dto.responses.UserAuthResponse;
import com.github.zigcat.greenhub.user_provider.kafka.dto.KafkaMessageTemplate;
import com.github.zigcat.greenhub.user_provider.kafka.jackson.JsonDeserializer;
import com.github.zigcat.greenhub.user_provider.kafka.jackson.JsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Value("${kafka.bootstrap-server}")
    private String BOOTSTRAP_SERVER;

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
    public KafkaTemplate<String, KafkaMessageTemplate<DTORequestible>> kafkaTemplate(
            ProducerFactory<String, KafkaMessageTemplate<DTORequestible>> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }
}
