package com.asyahir.statementprocessorservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MaybankDebitJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("[MaybankDebit.Job] Initiate Job. job={}", jobExecution);
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("[MaybankDebit.Job] Completed Job. job={}", jobExecution);
        JobExecutionListener.super.afterJob(jobExecution);
    }
}
