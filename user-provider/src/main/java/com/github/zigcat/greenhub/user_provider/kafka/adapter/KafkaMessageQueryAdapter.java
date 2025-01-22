package com.github.zigcat.greenhub.user_provider.kafka.adapter;

import com.github.zigcat.greenhub.user_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.user_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.user_provider.dto.mq.template.MessageTemplate;
import com.github.zigcat.greenhub.user_provider.events.RegisterAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.RegisterMessageQueryAdapterEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaMessageQueryAdapter implements MessageQueryAdapter {
    private KafkaSender<String, MessageTemplate<RegisterResponse>> kafkaRegisterResponseSender;
    private KafkaReceiver<String, MessageTemplate<RegisterRequest>> kafkaRegisterRequestReceiver;
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public KafkaMessageQueryAdapter(KafkaSender<String, MessageTemplate<RegisterResponse>> kafkaRegisterResponseSender, KafkaReceiver<String, MessageTemplate<RegisterRequest>> kafkaRegisterRequestReceiver, ApplicationEventPublisher applicationEventPublisher) {
        this.kafkaRegisterResponseSender = kafkaRegisterResponseSender;
        this.kafkaRegisterRequestReceiver = kafkaRegisterRequestReceiver;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void processRegisterMessage() {
        kafkaRegisterRequestReceiver.receive()
                .doOnNext(record -> {
                    String correlationId = record.key();
                    log.info("Received message with id {}", correlationId);
                    RegisterRequest requestData = record.value().getPayload();
                    log.info("Message: {}", requestData);
                    processRegistration(requestData, correlationId);
                })
                .doOnError(e -> log.error("Error while receiving message ", e))
                .subscribe();
    }

    private void processRegistration(RegisterRequest requestData, String correlationId){
        log.info("Preparing event...");
        CompletableFuture<RegisterAuthServiceReply> replyFuture =
                new CompletableFuture<>();
        RegisterMessageQueryAdapterEvent event =
                new RegisterMessageQueryAdapterEvent(this, requestData, replyFuture);
        log.info("Publishing event...");
        applicationEventPublisher.publishEvent(event);
        replyFuture.thenAccept(reply -> {
            RegisterResponse responseData = reply.getResponse();
            log.info("Event processed, received response {}", responseData);
            MessageTemplate<RegisterResponse> response = new MessageTemplate<>(responseData);
            sendRegisterResponse(response, correlationId);
        });
    }

    private void sendRegisterResponse(MessageTemplate<RegisterResponse> responseMessage, String correlationId){
        log.info("Preparing response message...");
        ProducerRecord<String, MessageTemplate<RegisterResponse>> response =
                new ProducerRecord<>("user-reg-topic-reply", correlationId, responseMessage);
        kafkaRegisterResponseSender.send(Mono.just(SenderRecord.create(response, correlationId)))
                .doOnNext(res -> log.info("Message sent successfully"))
                .doOnError(e -> log.error("Error while sending response ", e))
                .subscribe();
    }
}
