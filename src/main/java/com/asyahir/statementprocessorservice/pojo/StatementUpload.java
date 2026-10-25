package com.asyahir.statementprocessorservice.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatementUpload {
    private String name;
    private String bank;
    private String title;
    @JsonProperty("statement_date")
    private String statementDate;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("file_path")
    private String filePath;
}
