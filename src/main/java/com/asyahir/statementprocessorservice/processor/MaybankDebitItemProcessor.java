package com.asyahir.statementprocessorservice.processor;

import com.asyahir.statementprocessorservice.constant.StatementType;
import com.asyahir.statementprocessorservice.entity.Transaction;
import com.asyahir.statementprocessorservice.pojo.MaybankDebit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MaybankDebitItemProcessor implements ItemProcessor<MaybankDebit, Transaction> {
    @Override
    public Transaction process(@NonNull MaybankDebit item) throws Exception {

        String amt = item.getAmount();
        int operationIndex = StringUtils.length(amt) - 1;

        // Getting Amount and Operations
        char operation = amt.charAt(operationIndex);
        String amountStr = StringUtils.replace(StringUtils.left(amt, operationIndex), ",", StringUtils.EMPTY);
        Double amount = Double.parseDouble(amountStr);

        // Convert into LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate transactionDate = LocalDate.parse(item.getDate(), formatter);

        return Transaction.builder()
                .statementType(StatementType.MAYBANK_DEBIT)
                .userId(UUID.randomUUID().toString())
                .description(StringUtils.trim(item.getDescription()))
                .operation(operation)
                .amount(amount)
                .transactionDate(transactionDate)
                .createdDateTime(LocalDateTime.now())
                .build();
    }
}
