package com.github.zigcat.greenhub.api_gateway.kafka.adapter;

import com.github.zigcat.greenhub.api_gateway.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.api_gateway.dto.requests.JwtRequest;
import com.github.zigcat.greenhub.api_gateway.dto.responces.UserAuthResponse;
import com.github.zigcat.greenhub.api_gateway.dto.message.MessageTemplate;
import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private KafkaSender<String, MessageTemplate<JwtRequest>> kafkaJwtRequestSender;
    private KafkaReceiver<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseReceiver;

    @Autowired
    public KafkaMessageQueryAdapter(
            KafkaSender<String, MessageTemplate<JwtRequest>> kafkaJwtRequestSender,
            KafkaReceiver<String, MessageTemplate<UserAuthResponse>> kafkaUserResponseReceiver) {
        this.kafkaJwtRequestSender = kafkaJwtRequestSender;
        this.kafkaUserResponseReceiver = kafkaUserResponseReceiver;
    }

    @Override
    public Mono<UserAuthResponse> performAndAwait(JwtRequest data) {
        log.info("Preparing Kafka Record...");
        String correlationId = UUID.randomUUID().toString();
        MessageTemplate<JwtRequest> requestData = new MessageTemplate<>(data);
        ProducerRecord<String, MessageTemplate<JwtRequest>> request =
                new ProducerRecord<>("auth-topic", correlationId, requestData);
        log.info("Sending and awaiting...");
        return kafkaJwtRequestSender.send(Mono.just(SenderRecord.create(request, correlationId)))
                .doOnNext(result -> log.info("Message sent successfully with correlationId: {}", correlationId))
                .then(kafkaUserResponseReceiver.receive()
                        .doOnNext(record -> log.info("Received record with key: {}", record.key()))
                        .filter(record -> {
                            if(record.key().equals(correlationId)){
                                log.info("Correlated response captured");
                                if(record.value().getStatus() == 200){
                                    log.info("Status 200, reading...");
                                    return true;
                                } else {
                                    throw new AuthException(record.value().getMessage());
                                }
                            }
                            return false;
                        })
                        .map(record -> record.value().getPayload())
                        .timeout(Duration.ofSeconds(5))
                        .next()
                        .onErrorMap(AuthException.class, e -> {
                            log.error("Auth error: {}", e.getMessage());
                            return e;
                        })
                        .doOnError(e -> {
                            if (!(e instanceof AuthException)) {
                                log.error("Error receiving Kafka response", e);
                            }
                        })
                );
    }
}
