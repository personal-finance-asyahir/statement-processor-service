package com.asyahir.statementprocessorservice.config;

import com.asyahir.statementprocessorservice.entity.MaybankCredit;
import com.asyahir.statementprocessorservice.listener.MaybankCreditItemReadListener;
import com.asyahir.statementprocessorservice.listener.MaybankCreditItemWriteListener;
import com.asyahir.statementprocessorservice.pojo.MaybankCreditData;
import com.asyahir.statementprocessorservice.processor.MaybankCreditItemProcessor;
import com.asyahir.statementprocessorservice.reader.MaybankCreditItemReader;
import com.asyahir.statementprocessorservice.repository.MaybankCreditRepository;
import com.asyahir.statementprocessorservice.service.KafkaMessageService;
import com.asyahir.statementprocessorservice.writer.MaybankCreditItemWriter;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MaybankCreditBatchConfiguration {

    private static final String STEP1_NAME = "maybank-credit-step-1";

    private static final String TASK_NAME = "maybank-credit-task-async";

    @Bean
    @StepScope
    public ItemReader<MaybankCreditData> maybankCreditReader(@Value("#{jobParameters['input.statement.filepath']}") String resource){
        return new MaybankCreditItemReader(resource);
    }

    @Bean
    @StepScope
    public ItemProcessor<MaybankCreditData, MaybankCredit> maybankCreditProcessor(@Value("#{jobParameters['input.statement.userid']}") String userId) {
        return new MaybankCreditItemProcessor(userId);
    }

    @Bean
    public ItemWriter<MaybankCredit> maybankCreditWriter(MaybankCreditRepository maybankCreditRepository) {
        return new MaybankCreditItemWriter(maybankCreditRepository);
    }

    @Bean
    @StepScope
    public ItemReadListener<MaybankCreditData> maybankCreditItemReadListener(@Value("#{jobParameters['input.statement.userid']}") String userId) {
        return new MaybankCreditItemReadListener(userId);
    }

    @Bean
    @StepScope
    public ItemWriteListener<MaybankCredit> maybankCreditItemWriteListener(@Value("#{jobParameters['input.statement.userid']}") String userId,
                                                                           KafkaMessageService kafkaMessageService) {
        return new MaybankCreditItemWriteListener(userId, kafkaMessageService);
    }

    @Bean
    public TaskExecutor maybankCreditTaskExecutor() {
        return new SimpleAsyncTaskExecutor(TASK_NAME);
    }

    @Bean
    public Step maybankCreditStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ItemReader<MaybankCreditData> maybankCreditReader,
                                 ItemProcessor<MaybankCreditData, MaybankCredit> maybankCreditProcessor,
                                 ItemWriter<MaybankCredit> maybankCreditWriter,
                                 TaskExecutor maybankCreditTaskExecutor,
                                 ItemReadListener<MaybankCreditData> maybankCreditItemReadListener,
                                 ItemWriteListener<MaybankCredit> maybankCreditItemWriteListener){

        return new StepBuilder(STEP1_NAME, jobRepository)
                .<MaybankCreditData, MaybankCredit>chunk(10, transactionManager)
                .reader(maybankCreditReader)
                .listener(maybankCreditItemReadListener)
                .processor(maybankCreditProcessor)
                .writer(maybankCreditWriter)
                .listener(maybankCreditItemWriteListener)
                .taskExecutor(maybankCreditTaskExecutor)
                .build();
    }

}
