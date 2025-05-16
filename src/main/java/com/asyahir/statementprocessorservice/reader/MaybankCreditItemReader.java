package com.asyahir.statementprocessorservice.reader;

import com.asyahir.statementprocessorservice.pojo.MaybankCreditData;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

public class MaybankCreditItemReader implements ItemReader<MaybankCreditData> {

    private final List<MaybankCreditData> maybankCreditData = new ArrayList<>();

    public MaybankCreditItemReader(String pathfile) {
        StatementReader reader = new MaybankCreditStatementReader(pathfile);
        this.maybankCreditData.addAll(reader.read());
    }

    @Override
    public MaybankCreditData read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (CollectionUtils.isNotEmpty(maybankCreditData)) {
            return maybankCreditData.removeFirst();
        }
        return null;
    }
}
