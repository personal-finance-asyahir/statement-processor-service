package com.asyahir.statementprocessorservice.listener;

import com.asyahir.statementprocessorservice.constant.MessageQueueTopic;
import com.asyahir.statementprocessorservice.constant.Module;
import com.asyahir.statementprocessorservice.entity.MaybankDebit;
import com.asyahir.statementprocessorservice.service.KafkaMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

import java.util.List;

@Slf4j
public class MaybankDebitItemWriteListener implements ItemWriteListener<MaybankDebit> {

    private final String userId;

    private final KafkaMessageService kafkaMessageService;

    public MaybankDebitItemWriteListener(String userId,
                                         KafkaMessageService kafkaMessageService) {
        this.userId = userId;
        this.kafkaMessageService = kafkaMessageService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterWrite(Chunk<? extends MaybankDebit> items) {
        try {
            List<MaybankDebit> statements = (List<MaybankDebit>) items.getItems();

            this.kafkaMessageService.sendMessage(MessageQueueTopic.STATEMENT_PROCESS_MAYBANKDEBIT,
                    userId,
                    statements,
                    Module.MAYBANKDEBIT_ITEMWRITE);

            ItemWriteListener.super.afterWrite(items);
        } catch (Exception exception) {
            log.error("{} | Error. userId={}; exception={}", Module.MAYBANKDEBIT_ITEMWRITE, userId, exception.getMessage());
        }

    }
}
