package com.asyahir.statementprocessorservice.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatementUpload {
    private String bank;
    private String title;
    @JsonProperty("file_path")
    private String filePath;
}
