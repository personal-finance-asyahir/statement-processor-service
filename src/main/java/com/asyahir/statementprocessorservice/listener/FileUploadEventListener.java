package com.asyahir.statementprocessorservice.listener;

import com.asyahir.statementprocessorservice.pojo.StatementUpload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileUploadEventListener {

    private final ObjectMapper objectMapper;

    public FileUploadEventListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "file.upload")
    public void statementUpload(@Payload String request,
                                @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                                @Header("created-at") String createdAt) throws JsonProcessingException {

        System.out.println("Syahir test request" + request);
        List<StatementUpload> statementUploads = objectMapper.readValue(request, new TypeReference<List<StatementUpload>>() {});
        System.out.println("Syahir test request2" + statementUploads);
        System.out.println("Syahir test key" + key);
        System.out.println("Syahir test createdAt" + createdAt);

    }
}
