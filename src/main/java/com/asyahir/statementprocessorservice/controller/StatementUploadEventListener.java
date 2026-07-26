package com.asyahir.statementprocessorservice.controller;

import com.asyahir.statementprocessorservice.pojo.StatementUpload;

import org.apache.commons.lang3.Strings;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Component
public class StatementUploadEventListener {

    private final ObjectMapper objectMapper;
    
    private final JobOperator jobOperator;

    private final Job maybankDebitJob;

    private final Job maybankCreditJob;


    public StatementUploadEventListener(ObjectMapper objectMapper,
                                        Job maybankDebitJob,
                                        Job maybankCreditJob,
                                        JobOperator jobOperator) {
        this.objectMapper = objectMapper;
        this.maybankDebitJob = maybankDebitJob;
        this.maybankCreditJob = maybankCreditJob;
        this.jobOperator = jobOperator;
    }

    @KafkaListener(topics = "statement.upload")
    public void statementUpload(@Payload String request,
                                @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                                @Header(name = KafkaHeaders.CORRELATION_ID, required = false) String corrId) throws Exception {

        List<StatementUpload> statementUploads = objectMapper.readValue(request, new TypeReference<List<StatementUpload>>() {});

        for (StatementUpload su : statementUploads) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("input.statement.filepath", su.getFilePath())
                    .addString("input.statement.userid", key)
                    .addString("input.statement.job", UUID.randomUUID().toString())
                    .toJobParameters();

            if (Strings.CI.contains(su.getBank(), "Maybank") &&
                    Strings.CI.contains(su.getTitle(), "Saving")) {
                jobOperator.start(maybankDebitJob, jobParameters);
            } else if (Strings.CI.contains(su.getBank(), "Maybank") &&
                    Strings.CI.contains(su.getTitle(), "Credit")) {
                jobOperator.start(maybankCreditJob, jobParameters);
            }
        }

    }
}
