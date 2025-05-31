package com.asyahir.statementprocessorservice.pojo;

import com.asyahir.statementprocessorservice.entity.MaybankDebit;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class MaybankDebitJson {
    private UUID userId;
    private Double amount;
    private Double statementBalance;
    private String description;
    private char operation;
    private LocalDate transactionDate;

    public MaybankDebitJson(MaybankDebit debit) {
        this.userId = debit.getUserId();
        this.amount = debit.getAmount();
        this.statementBalance = debit.getStatementBalance();
        this.description = debit.getDescription();
        this.operation = debit.getOperation();
        this.transactionDate = debit.getTransactionDate();
    }
}
