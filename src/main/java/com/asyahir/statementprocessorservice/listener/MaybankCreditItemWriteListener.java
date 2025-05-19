package com.asyahir.statementprocessorservice.listener;

import com.asyahir.statementprocessorservice.constant.MessageQueueTopic;
import com.asyahir.statementprocessorservice.constant.Module;
import com.asyahir.statementprocessorservice.entity.MaybankCredit;
import com.asyahir.statementprocessorservice.service.KafkaMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

import java.util.List;

@Slf4j
public class MaybankCreditItemWriteListener implements ItemWriteListener<MaybankCredit> {

    private final String userId;

    private final KafkaMessageService kafkaMessageService;

    public MaybankCreditItemWriteListener(String userId,
                                          KafkaMessageService kafkaMessageService) {
        this.userId = userId;
        this.kafkaMessageService = kafkaMessageService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterWrite(Chunk<? extends MaybankCredit> items) {
        try {
            List<MaybankCredit> statements = (List<MaybankCredit>) items.getItems();

            this.kafkaMessageService.sendMessage(MessageQueueTopic.STATEMENT_PROCESS_MAYBANKCREDIT,
                    userId,
                    statements,
                    Module.MAYBANKCREDIT_ITEMWRITE);

            ItemWriteListener.super.afterWrite(items);
        } catch (Exception exception) {
            log.error("{} | Error. userId={}, exception={}", Module.MAYBANKCREDIT_ITEMWRITE, userId, exception.getMessage());
        }

    }
}
