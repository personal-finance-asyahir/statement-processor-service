package com.asyahir.statementprocessorservice.writer;

import com.asyahir.statementprocessorservice.entity.Transaction;
import com.asyahir.statementprocessorservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

public class MaybankDebitItemWriter implements ItemWriter<Transaction> {

    private final TransactionRepository transactionRepository;

    public MaybankDebitItemWriter(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void write(@NonNull Chunk<? extends Transaction> chunk) throws Exception {
        chunk.getItems().forEach(transactionRepository::save);
    }
}
