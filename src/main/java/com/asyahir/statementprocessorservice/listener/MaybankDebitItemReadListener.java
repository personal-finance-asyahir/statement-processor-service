package com.asyahir.statementprocessorservice.listener;

import com.asyahir.statementprocessorservice.pojo.MaybankDebitData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MaybankDebitItemReadListener implements ItemReadListener<MaybankDebitData> {

    @Override
    public void afterRead(MaybankDebitData item) {
        log.info("[MaybankDebit.ItemRead] Reading item. debit={}", item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("[MaybankDebit.ItemRead] Error reading item. ", ex);
    }
}
