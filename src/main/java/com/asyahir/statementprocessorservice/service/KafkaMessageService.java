package com.asyahir.statementprocessorservice.service;

import com.asyahir.statementprocessorservice.constant.MessageQueueTopic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public final class KafkaMessageService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaMessageService(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(String topic, String key, List<?> data, String module) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(data);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("{} | Error sending kafka message. topic={}; key={}; exception={}", module, topic, key, exception.getMessage());
            }
            log.info("{} | Message sent to kafka. topic={}; key={}; message={}", module, topic, key, message);
        });
    }
}
