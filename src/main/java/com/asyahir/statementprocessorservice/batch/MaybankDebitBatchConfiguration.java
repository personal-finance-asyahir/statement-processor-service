package com.asyahir.statementprocessorservice.batch;

import com.asyahir.statementprocessorservice.pojo.MaybankDebit;
import com.asyahir.statementprocessorservice.reader.MaybankDebitItemReader;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MaybankDebitBatchConfiguration {

    @Bean
    public ItemReader<MaybankDebit> reader(@Value("#{jobParameters[input.file.name]") String resource) throws IOException {
        return new MaybankDebitItemReader(resource);
    }
}
