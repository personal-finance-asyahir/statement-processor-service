package com.asyahir.statementprocessorservice.processor;

import com.asyahir.statementprocessorservice.entity.MaybankDebit;
import com.asyahir.statementprocessorservice.pojo.MaybankDebitData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MaybankDebitItemProcessor implements ItemProcessor<MaybankDebitData, MaybankDebit> {

    private final String userId;

    public MaybankDebitItemProcessor(String userId) {
        this.userId = userId;
    }

    @Override
    public MaybankDebit process(@NonNull MaybankDebitData item) throws Exception {

        String amt = item.getAmount();
        int operationIndex = StringUtils.length(amt) - 1;

        // Getting Amount and Operations
        char operation = amt.charAt(operationIndex);
        String amountStr = this.removeCommas(StringUtils.left(amt, operationIndex));
        Double amount = Double.parseDouble(amountStr);

        // Convert into LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate transactionDate = LocalDate.parse(item.getDate(), formatter);

        // Statement Balance
        Double statementBalance = Double.parseDouble(this.removeCommas(item.getStatementBalance()));

        return MaybankDebit.builder()
                .userId(UUID.fromString(userId))
                .description(StringUtils.trim(item.getDescription()))
                .operation(operation)
                .amount(amount)
                .statementBalance(statementBalance)
                .transactionDate(transactionDate)
                .insertedDateTime(LocalDateTime.now())
                .build();
    }

    private String removeCommas(String amount) {
        return StringUtils.replace(amount, ",", StringUtils.EMPTY);
    }
}
