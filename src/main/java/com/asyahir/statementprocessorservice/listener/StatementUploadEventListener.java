package com.asyahir.statementprocessorservice.listener;

import com.asyahir.statementprocessorservice.pojo.StatementUpload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StatementUploadEventListener {

    private final ObjectMapper objectMapper;

    private final JobLauncher jobLauncher;

    private final Job maybankDebitJob;

    private final Job maybankCreditJob;


    public StatementUploadEventListener(ObjectMapper objectMapper, JobLauncher jobLauncher,
                                        Job maybankDebitJob, Job maybankCreditJob) {
        this.objectMapper = objectMapper;
        this.jobLauncher = jobLauncher;
        this.maybankDebitJob = maybankDebitJob;
        this.maybankCreditJob = maybankCreditJob;
    }

    @KafkaListener(topics = "statement.upload")
    public void statementUpload(@Payload String request,
                                @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                                @Header(name = KafkaHeaders.CORRELATION_ID, required = false) String corrId) throws JsonProcessingException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        List<StatementUpload> statementUploads = objectMapper.readValue(request, new TypeReference<List<StatementUpload>>() {});

        for (StatementUpload su : statementUploads) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("input.statement.filepath", su.getFilePath())
                    .addString("input.statement.userid", key)
                    .addString("input.statement.job", UUID.randomUUID().toString())
                    .toJobParameters();

            if (StringUtils.containsIgnoreCase(su.getBank(), "Maybank") &&
            StringUtils.containsIgnoreCase(su.getTitle(), "Saving")) {
                jobLauncher.run(maybankDebitJob, jobParameters);
            } else if (StringUtils.containsIgnoreCase(su.getBank(), "Maybank") &&
                    StringUtils.containsIgnoreCase(su.getTitle(), "Credit")) {
                jobLauncher.run(maybankCreditJob, jobParameters);
            }
        }

    }
}
