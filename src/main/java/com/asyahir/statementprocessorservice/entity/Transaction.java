package com.asyahir.statementprocessorservice.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {
    private String id;
    private String type;
    private String amount;
    private String description;
    private String operation;
    private String date;
}
