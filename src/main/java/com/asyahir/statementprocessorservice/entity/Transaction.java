package com.asyahir.statementprocessorservice.entity;

import com.asyahir.statementprocessorservice.constants.StatementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String userId;
    private StatementType statementType;
    private Double amount;
    private String description;
    private String category;
    private char operation;
    private LocalDate transactionDate;
    private LocalDateTime createdDateTime;
}
