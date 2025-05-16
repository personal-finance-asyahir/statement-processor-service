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
<<<<<<< HEAD
        maybankDebitRepository.saveAll(chunk.getItems());
=======
        chunk.getItems().forEach(maybankDebitRepository::save);
>>>>>>> 8459a0c43d0b4f5f5953a2f0b49e71581a400a99
    }
}
