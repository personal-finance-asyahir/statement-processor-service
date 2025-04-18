package com.asyahir.statementprocessorservice.processor;

import com.asyahir.statementprocessorservice.entity.Transaction;
import com.asyahir.statementprocessorservice.pojo.MaybankDebit;
import org.springframework.batch.item.ItemProcessor;

public class MaybankDebitItemProcessor implements ItemProcessor<MaybankDebit, Transaction> {
    @Override
    public Transaction process(MaybankDebit item) throws Exception {
        return null;
    }
}
