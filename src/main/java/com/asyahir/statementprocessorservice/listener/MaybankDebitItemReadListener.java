package com.asyahir.statementprocessorservice.listener;

import com.asyahir.statementprocessorservice.constant.Module;
import com.asyahir.statementprocessorservice.pojo.MaybankDebitData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class MaybankDebitItemReadListener implements ItemReadListener<MaybankDebitData> {

    private final String userId;

    public MaybankDebitItemReadListener(String userId) {
        this.userId = userId;
    }

    @Override
    public void afterRead(MaybankDebitData item) {
        log.info("{} | Reading item. userId={}; debit={}", Module.MAYBANKDEBIT_ITEMREAD, userId, item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("{} | Error. userId={}; exception={}. ", Module.MAYBANKDEBIT_ITEMREAD, userId,  ex.getMessage());
    }
}
