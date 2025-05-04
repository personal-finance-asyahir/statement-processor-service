package com.asyahir.statementprocessorservice.pojo;

import lombok.Data;

import java.util.List;

@Data
public class StatementUploadRequest {
    private List<StatementUpload> statements;
}
