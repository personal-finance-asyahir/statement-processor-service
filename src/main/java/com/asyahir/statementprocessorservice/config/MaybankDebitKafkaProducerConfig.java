//package com.asyahir.statementprocessorservice.config;
//
//import com.asyahir.statementprocessorservice.entity.MaybankDebit;
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.UUIDSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Configuration
//public class MaybankDebitKafkaProducerConfig {
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    String bootstrapServers;
//
//    @Bean
//    public ProducerFactory<String, List<MaybankDebit>> producerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        JsonSerializer<List<MaybankDebit>> serializer = new JsonSerializer<>() {
//            @Override
//            public void serialize(List<MaybankDebit> maybankDebits, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//
//            }
//        };
//
//        return new DefaultKafkaProducerFactory<>(configProps, new JsonSerializer(List<MaybankDebit.class>.getClass()));
//    }
//
//    @Bean
//    public KafkaTemplate<String, List<MaybankDebit>> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//}
