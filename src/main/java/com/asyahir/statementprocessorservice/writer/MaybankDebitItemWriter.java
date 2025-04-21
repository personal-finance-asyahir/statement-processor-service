package com.asyahir.statementprocessorservice.writer;

import com.asyahir.statementprocessorservice.entity.Transaction;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

public class MaybankDebitItemWriter<Transaction> implements ItemWriter<Transaction> {

    @Override
    public void write(@NonNull Chunk<? extends Transaction> chunk) throws Exception {

    }
}
