package com.asyahir.statementprocessorservice.writer;

import com.asyahir.statementprocessorservice.entity.MaybankDebit;
import com.asyahir.statementprocessorservice.repository.MaybankDebitRepository;
import jakarta.transaction.Transactional;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

public class MaybankDebitItemWriter implements ItemWriter<MaybankDebit> {

    private final MaybankDebitRepository maybankDebitRepository;

    public MaybankDebitItemWriter(MaybankDebitRepository maybankDebitRepository) {
        this.maybankDebitRepository = maybankDebitRepository;
    }

    @Override
    @Transactional
    public void write(@NonNull Chunk<? extends MaybankDebit> chunk) throws Exception {
        chunk.getItems().forEach(maybankDebitRepository::save);
    }
}
