package com.asyahir.statementprocessorservice.reader;

import com.asyahir.statementprocessorservice.pojo.MaybankDebitData;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.infrastructure.item.ItemReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MaybankDebitItemReader implements ItemReader<MaybankDebitData> {

    private final List<MaybankDebitData> maybankDebitData = new ArrayList<>();

    public MaybankDebitItemReader(String pathfile) throws IOException {
        StatementReader reader = new MaybankDebitStatementReader(pathfile);
        this.maybankDebitData.addAll(reader.read());
    }

    @Override
    public MaybankDebitData read() throws Exception {
        if (CollectionUtils.isNotEmpty(maybankDebitData)) {
            return maybankDebitData.removeFirst();
        }
        return null;
    }
}
