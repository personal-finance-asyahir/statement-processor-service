package com.asyahir.statementprocessorservice.reader;

import com.asyahir.statementprocessorservice.pojo.MaybankDebitData;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MaybankDebitItemReader implements ItemReader<MaybankDebitData> {

    private final List<MaybankDebitData> maybankDebitData;

    public MaybankDebitItemReader(String pathfile) throws IOException {
        File file = new ClassPathResource(pathfile).getFile();
        MaybankDebitStatementReader reader = new MaybankDebitStatementReader();
        maybankDebitData = reader.read(file);
    }

    @Override
    public MaybankDebitData read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (CollectionUtils.isNotEmpty(maybankDebitData)) {
            return maybankDebitData.removeFirst();
        }
        return null;
    }
}
