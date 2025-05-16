//package com.asyahir.statementprocessorservice.reader;
//
//import com.asyahir.statementprocessorservice.pojo.MaybankCreditData;
//import org.springframework.batch.item.ItemReader;
//
//import java.io.File;
//import java.util.List;
//
//public class MaybankCreditItemReader implements ItemReader<MaybankCreditData> {
//
//    private final List<MaybankCreditData> maybankCreditData;
//
//    public MaybankCreditItemReader(String pathfile) {
//        File file = new File(pathfile);
//        MaybankCreditStatementReader reader = new MaybankCreditStatementReader();
//        maybankCreditData = reader.read(file);
//    }
//}
