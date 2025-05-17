package com.asyahir.statementprocessorservice.listener;

import com.asyahir.statementprocessorservice.constant.Module;
import com.asyahir.statementprocessorservice.pojo.MaybankCreditData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class MaybankCreditItemReadListener implements ItemReadListener<MaybankCreditData> {

    private final String userId;

    public MaybankCreditItemReadListener(String userId) {
        this.userId = userId;
    }

    @Override
    public void afterRead(MaybankCreditData item) {
        log.info("{} | Reading item. userId={}; credit={}", Module.MAYBANKCREDIT_ITEMREAD, userId, item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("{} | Read Error. userId={}; exception={}. ",Module.MAYBANKCREDIT_ITEMREAD, userId, ex.getMessage());
    }
}
