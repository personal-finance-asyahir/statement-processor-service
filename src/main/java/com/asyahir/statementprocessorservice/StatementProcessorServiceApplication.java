package com.asyahir.statementprocessorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StatementProcessorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatementProcessorServiceApplication.class, args);
    }

}
