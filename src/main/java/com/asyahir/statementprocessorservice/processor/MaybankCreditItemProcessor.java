package com.asyahir.statementprocessorservice.processor;

import com.asyahir.statementprocessorservice.entity.MaybankCredit;
import com.asyahir.statementprocessorservice.pojo.MaybankCreditData;
import org.springframework.batch.item.ItemProcessor;

public class MaybankCreditItemProcessor implements ItemProcessor<MaybankCreditData, MaybankCredit> {

    private final String userId;

    public MaybankCreditItemProcessor(String userId) {
        this.userId = userId;
    }

    @Override
    public MaybankCredit process(MaybankCreditData item) throws Exception {
        return null;
    }
}
