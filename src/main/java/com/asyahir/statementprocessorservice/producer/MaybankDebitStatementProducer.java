//package com.asyahir.statementprocessorservice.producer;
//
//import com.asyahir.statementprocessorservice.entity.MaybankDebit;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//@Component
//@Slf4j
//public class MaybankDebitStatementProducer implements StatementProducer<MaybankDebit> {
//
//    private final KafkaTemplate<String, List<MaybankDebit>> kafkaTemplate;
//
//    @Autowired
//    public MaybankDebitStatementProducer(KafkaTemplate<String, List<MaybankDebit>> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @Override
//    public void produce(List<MaybankDebit> statements) {
//        String keyId = String.valueOf(statements.getLast().getUserId());
//        String topic = "statement.process";
//        CompletableFuture<SendResult<String, List<MaybankDebit>>> future = kafkaTemplate.send(topic, keyId, statements);
//        future.whenComplete((result, exception) -> {
//            if (exception != null) {
//                log.error("Error while sending kafka message. topic={}, key={}", topic, keyId, exception);
//            }
//            log.info("Message sent to kafka topic. topic={}, key={}", topic, keyId);
//        });
//    }
//}
