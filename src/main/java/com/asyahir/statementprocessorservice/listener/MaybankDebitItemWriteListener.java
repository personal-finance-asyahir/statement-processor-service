package com.asyahir.statementprocessorservice.listener;

import com.asyahir.statementprocessorservice.entity.MaybankDebit;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class MaybankDebitItemWriteListener implements ItemWriteListener<MaybankDebit> {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public MaybankDebitItemWriteListener(KafkaTemplate<String, String> kafkaTemplate,
                                         ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterWrite(Chunk<? extends MaybankDebit> items) {
        try {
            List<MaybankDebit> statements = (List<MaybankDebit>) items.getItems();
            String keyId = String.valueOf(statements.getLast().getUserId());
            String topic = "statement.process";
            String statementData = objectMapper.writeValueAsString(statements);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, keyId, statementData);
            future.whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Error while sending kafka message. topic={}, key={}", topic, keyId, exception);
                }
                log.info("Message sent to kafka topic. topic={}, key={}, statementData={}", topic, keyId, statementData);
            });
            ItemWriteListener.super.afterWrite(items);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }

    }
}
