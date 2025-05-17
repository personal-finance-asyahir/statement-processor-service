package com.asyahir.statementprocessorservice.processor;

import com.asyahir.statementprocessorservice.entity.MaybankCredit;
import com.asyahir.statementprocessorservice.pojo.MaybankCreditData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.UUID;

public class MaybankCreditItemProcessor implements ItemProcessor<MaybankCreditData, MaybankCredit> {

    private final String userId;

    public MaybankCreditItemProcessor(String userId) {
        this.userId = userId;
    }

    @Override
    public MaybankCredit process(@NonNull MaybankCreditData item) throws Exception {

        char operation = '-';

        String amount = StringUtils.replace(item.getAmount(), ",", StringUtils.EMPTY);
        if (StringUtils.contains(amount, "CR")) {
            operation = '+';
            amount = StringUtils.replace(amount, "CR", "");
        }

        // Statement Date Year
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd MMM yy")
                .toFormatter(Locale.ENGLISH);
        LocalDate statementDate = LocalDate.parse(item.getStatementDate(), formatter);

        // Posting Date
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM");
        MonthDay postDateDay = MonthDay.parse(item.getPostingDate(), formatter2);
        LocalDate postDateYear = LocalDate.of(statementDate.getYear(), postDateDay.getMonth(), postDateDay.getDayOfMonth());

        // Transaction Date
        MonthDay trxDateDay = MonthDay.parse(item.getDate(), formatter2);
        LocalDate trxDateYear = LocalDate.of(statementDate.getYear(), trxDateDay.getMonth(), trxDateDay.getDayOfMonth());

        return MaybankCredit.builder()
                .userId(UUID.fromString(this.userId))
                .description(StringUtils.trim(item.getDescription()))
                .operation(operation)
                .amount(Double.parseDouble(amount))
                .postingDate(postDateYear)
                .transactionDate(trxDateYear)
                .insertedDateTime(LocalDateTime.now()).build();
    }
}
