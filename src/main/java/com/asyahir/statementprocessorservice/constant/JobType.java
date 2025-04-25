package com.asyahir.statementprocessorservice.constant;

public enum JobType {

    MAYBANK_DEBIT("maybank-debit-job"),
    MAYBANK_CREDIT("maybank-credit-job"),
    CIMB_DEBIT("cimb-debit-job"),
    CIMB_CREDIT("cimb-credit-job");

    private final String jobName;

    JobType(String jobName) {
        this.jobName = jobName;
    }

    public String jobName() {
        return jobName;
    }
}
