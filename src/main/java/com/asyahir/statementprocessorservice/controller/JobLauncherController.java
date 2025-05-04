package com.asyahir.statementprocessorservice.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class JobLauncherController {

    private final JobLauncher jobLauncher;

    private final Job maybankDebitJob;


    @Autowired
    public JobLauncherController(JobLauncher jobLauncher, Job maybankDebitJob) {
        this.jobLauncher = jobLauncher;
        this.maybankDebitJob = maybankDebitJob;
    }

    @Scheduled(fixedDelay = 1000)
    public void handle() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        var jobParameters = new JobParametersBuilder()
                .addString("input.file.name", "/Users/syahirghariff/Developer/personal-finance-project/bank_statement/mypdf.pdf")
                .addString("input.file.userid", UUID.randomUUID().toString())
                .toJobParameters();
        jobLauncher.run(maybankDebitJob, jobParameters);
    }
}
