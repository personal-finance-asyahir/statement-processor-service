package com.asyahir.statementprocessorservice.controller;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class JobLauncherController {

    private final JobOperator jobOperator;

    private final Job maybankDebitJob;


    @Autowired
    public JobLauncherController(JobOperator jobOperator, Job maybankDebitJob) {
        this.jobOperator = jobOperator;
        this.maybankDebitJob = maybankDebitJob;
    }

//    @Scheduled(fixedDelay = 1000)
    public void handle() throws Exception {
        var jobParameters = new JobParametersBuilder()
                .addString("input.file.name", "/Users/syahirghariff/Developer/personal-finance-project/bank_statement/mypdf.pdf")
                .addString("input.file.userid", UUID.randomUUID().toString())
                .toJobParameters();
        jobOperator.start(maybankDebitJob, jobParameters);
    }
}
