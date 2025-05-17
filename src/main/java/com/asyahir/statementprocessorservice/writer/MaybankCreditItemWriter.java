package com.asyahir.statementprocessorservice.writer;

import com.asyahir.statementprocessorservice.entity.MaybankCredit;
import com.asyahir.statementprocessorservice.repository.MaybankCreditRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class MaybankCreditItemWriter implements ItemWriter<MaybankCredit> {

    private final MaybankCreditRepository maybankCreditRepository;

    public MaybankCreditItemWriter(MaybankCreditRepository maybankCreditRepository) {
        this.maybankCreditRepository = maybankCreditRepository;
    }

    @Override
    public void write(Chunk<? extends MaybankCredit> chunk) throws Exception {
        maybankCreditRepository.saveAll(chunk.getItems());
    }
}
