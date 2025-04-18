package com.asyahir.statementprocessorservice.entity;

import com.asyahir.statementprocessorservice.constants.StatementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String id;
    private StatementType type;
    private Double amount;
    private String description;
    private char operation;
    private LocalDateTime date;
}
