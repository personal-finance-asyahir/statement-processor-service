package com.asyahir.statementprocessorservice.config;

import com.asyahir.statementprocessorservice.entity.MaybankDebit;
import com.asyahir.statementprocessorservice.listener.MaybankDebitItemReadListener;
import com.asyahir.statementprocessorservice.listener.MaybankDebitItemWriteListener;
import com.asyahir.statementprocessorservice.pojo.MaybankDebitData;
import com.asyahir.statementprocessorservice.processor.MaybankDebitItemProcessor;
import com.asyahir.statementprocessorservice.reader.MaybankDebitItemReader;
import com.asyahir.statementprocessorservice.repository.MaybankDebitRepository;
import com.asyahir.statementprocessorservice.service.KafkaMessageService;
import com.asyahir.statementprocessorservice.writer.MaybankDebitItemWriter;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ItemReadListener;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
public class MaybankDebitBatchConfiguration {

    private static final String STEP1_NAME = "maybank-debit-step-1";

    private static final String TASK_NAME = "maybank-debit-task-async";

    @Bean
    @StepScope
    public ItemReader<MaybankDebitData> maybankDebitReader(@Value("#{jobParameters['input.statement.filepath']}") String resource) throws IOException {
        return new MaybankDebitItemReader(resource);
    }

    @Bean
    @StepScope
    public ItemProcessor<MaybankDebitData, MaybankDebit> maybankDebitProcessor(@Value("#{jobParameters['input.statement.userid']}") String userId) {
        return new MaybankDebitItemProcessor(userId);
    }

    @Bean
    public ItemWriter<MaybankDebit> maybankDebitWriter(MaybankDebitRepository maybankDebitRepository) {
        return new MaybankDebitItemWriter(maybankDebitRepository);
    }

    @Bean
    @StepScope
    public ItemReadListener<MaybankDebitData> maybankDebitItemReadListener(@Value("#{jobParameters['input.statement.userid']}") String userId) {
        return new MaybankDebitItemReadListener(userId);
    }

    @Bean
    @StepScope
    public ItemWriteListener<MaybankDebit> maybankDebitItemWriteListener(@Value("#{jobParameters['input.statement.userid']}") String userId,
                                                                         KafkaMessageService kafkaMessageService) {
        return new MaybankDebitItemWriteListener(userId, kafkaMessageService);
    }

    @Bean
    public AsyncTaskExecutor maybankDebitTaskExecutor() {
        return new SimpleAsyncTaskExecutor(TASK_NAME);
    }

    @Bean
    public Step maybankDebitStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ItemReader<MaybankDebitData> maybankDebitReader,
                                 ItemProcessor<MaybankDebitData, MaybankDebit> maybankDebitProcessor,
                                 ItemWriter<MaybankDebit> maybankDebitWriter,
                                 AsyncTaskExecutor maybankDebitTaskExecutor,
                                 ItemReadListener<MaybankDebitData> maybankDebitItemReadListener,
                                 ItemWriteListener<MaybankDebit> maybankDebitItemWriteListener){

        return new StepBuilder(STEP1_NAME, jobRepository)
                .<MaybankDebitData, MaybankDebit>chunk(10)
                .reader(maybankDebitReader)
                .listener(maybankDebitItemReadListener)
                .processor(maybankDebitProcessor)
                .writer(maybankDebitWriter)
                .listener(maybankDebitItemWriteListener)
                .taskExecutor(maybankDebitTaskExecutor)
                .build();
    }

}
