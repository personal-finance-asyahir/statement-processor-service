package com.asyahir.statementprocessorservice.batch;

import com.asyahir.statementprocessorservice.entity.Transaction;
import com.asyahir.statementprocessorservice.pojo.MaybankDebit;
import com.asyahir.statementprocessorservice.processor.MaybankDebitItemProcessor;
import com.asyahir.statementprocessorservice.reader.MaybankDebitItemReader;
import com.asyahir.statementprocessorservice.repository.TransactionRepository;
import com.asyahir.statementprocessorservice.writer.MaybankDebitItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableBatchProcessing(tablePrefix = "statement_processor_db.batch_")
public class MaybankDebitBatchConfiguration {

    @Bean
    @StepScope
    public ItemReader<MaybankDebit> reader(@Value("#{jobParameters['input.file.name']}") String resource) throws IOException {
        return new MaybankDebitItemReader(resource);
    }

    @Bean
    public ItemProcessor<MaybankDebit, Transaction> processor() {
        return new MaybankDebitItemProcessor();
    }

    @Bean
    public ItemWriter<Transaction> writer(TransactionRepository transactionRepository) {
        return new MaybankDebitItemWriter(transactionRepository);
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      ItemReader<MaybankDebit> reader,
                      ItemProcessor<MaybankDebit, Transaction> processor,
                      ItemWriter<Transaction> writer){

        return new StepBuilder("maybankDebitStep1", jobRepository)
                .<MaybankDebit, Transaction>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job maybankDebitJob (JobRepository jobRepository, Step step1) {
        var name = "maybankDebitJob" + LocalDateTime.now();
        return new JobBuilder(name, jobRepository)
                .start(step1)
                .build();
    }

}
