package com.asyahir.statementprocessorservice.processor;

import com.asyahir.statementprocessorservice.constants.StatementType;
import com.asyahir.statementprocessorservice.entity.Transaction;
import com.asyahir.statementprocessorservice.pojo.MaybankDebit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;

public class MaybankDebitItemProcessor implements ItemProcessor<MaybankDebit, Transaction> {
    @Override
    public Transaction process(@NonNull MaybankDebit item) throws Exception {

        String amt = item.getAmount();
        int amountLength = StringUtils.length(amt) - 1;

        // Getting Amount and Operations
        char operation = amt.charAt(amountLength);
        double amount = Double.parseDouble(StringUtils.left(amt, amountLength));

        LocalDateTime transactionDate = LocalDateTime.parse(item.getDate());

        return Transaction.builder()
                .type(StatementType.MAYBANK_DEBIT)
                .description(StringUtils.trim(item.getDescription()))
                .operation(operation)
                .amount(amount)
                .date(transactionDate)
                .build();
    }
}
