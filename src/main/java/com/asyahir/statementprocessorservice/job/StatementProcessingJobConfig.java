package com.asyahir.statementprocessorservice.job;

import com.asyahir.statementprocessorservice.listener.MaybankDebitJobExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.asyahir.statementprocessorservice.constant.JobType.*;

@Configuration
@EnableBatchProcessing(tablePrefix = "statement_processor_db.batch_")
public class StatementProcessingJobConfig {

    @Bean
    public Job maybankDebitJob(JobRepository jobRepository,
                               Step maybankDebitStep,
                               MaybankDebitJobExecutionListener jobExecutionListener) {
        return new JobBuilder(MAYBANK_DEBIT.jobName(), jobRepository)
                .listener(jobExecutionListener)
                .start(maybankDebitStep)
                .build();
    }
}

