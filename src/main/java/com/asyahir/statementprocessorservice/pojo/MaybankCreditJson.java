package com.asyahir.statementprocessorservice.pojo;

import com.asyahir.statementprocessorservice.entity.MaybankCredit;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class MaybankCreditJson {
    private UUID userId;
    private Double amount;
    private String description;
    private char operation;
    private LocalDate postingDate;
    private LocalDate transactionDate;

    public MaybankCreditJson(MaybankCredit credit) {
        this.userId = credit.getUserId();
        this.amount = credit.getAmount();
        this.description = credit.getDescription();
        this.operation = credit.getOperation();
        this.postingDate = credit.getPostingDate();
        this.transactionDate = credit.getTransactionDate();
    }
}
