package com.asyahir.statementprocessorservice.reader;

import com.asyahir.statementprocessorservice.pojo.MaybankCreditData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class MaybankCreditStatementReader extends StatementReader<MaybankCreditData>{

    private final String TEXT_SPLITTER = "_____";

    private final List<MaybankCreditData> allCredits = new ArrayList<>();

    private String statementDate;

    private String paymentDueDate;

    public MaybankCreditStatementReader(String filepath){
        super(filepath);
    }

    public static void main (String[] args) {
        StatementReader reader = new MaybankCreditStatementReader("/Users/syahirghariff/Developer/personal-finance-project/bank_statement/0394050410542100_20250312.pdf");
        reader.read();
    }

    public List<MaybankCreditData> read() {

        try {
            PDDocument document = Loader.loadPDF(this.file);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();
            while (pi.hasNext()) {
                Page page = pi.next();

                List<Table> tables = sea.extract(page);

                int pageNumber = page.getPageNumber();

                Table mytable = pageNumber == 1 ? tables.get(2) : null;
                if (mytable != null){
                    List<List<RectangularTextContainer>> rows = mytable.getRows();
                    if (CollectionUtils.isNotEmpty(rows)){
                        rows.removeFirst();
                    }
                    for (List<RectangularTextContainer> cells : rows) {
                        for (int k = 0; k < cells.size(); k++) {
                            RectangularTextContainer<TextChunk> content = cells.get(k);
                            String text = content.getText().replace("\r", "");
                            if (k==0) {
                                this.statementDate = text;
                            } else if (k==1) {
                                this.paymentDueDate = text;
                            }
                        }
                    }
                }

                Table table = pageNumber == 1 ? tables.getLast() : tables.get(2);

                List<List<RectangularTextContainer>> rows = table.getRows();
                if (CollectionUtils.isNotEmpty(rows)){
                    rows.removeFirst();
                }

                for (List<RectangularTextContainer> cells : rows) {
                    List<MaybankCreditData> credits = new ArrayList<>();
                    for (int k = 0; k < cells.size(); k++) {
                        RectangularTextContainer<TextChunk> content = cells.get(k);
                        List<String> items = this.getItems(content);

                        switch(k) {
                            case 0: // Posting Date
                                if (pageNumber == 1) {
                                    items.removeFirst();
                                }
                                credits.addAll(this.generateCreditList(items));
                                break;
                            case 1: // Transaction Date
                                if (pageNumber == 1) {
                                    items.removeFirst();
                                }
                                this.updateCredits(credits, items, MaybankCreditData::setDate);
                                break;
                            case 2: // Transaction Description
                                items = this.getDescriptionItems(content);
                                if (pageNumber == 1) {
                                    items = items.subList(8, CollectionUtils.size(items));
                                }
                                this.updateCredits(credits, items, MaybankCreditData::setDescription);
                                break;
                            case 3: // Amount
                                if (pageNumber == 1) {
                                    items.removeFirst();
                                }
                                if (CollectionUtils.size(items) > CollectionUtils.size(credits)) {
                                    items.removeLast();
                                }
                                this.updateCredits(credits, items, MaybankCreditData::setAmount);
                                break;
                        }
                    }
                    this.allCredits.addAll(credits);
                }
            }
            return allCredits;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<MaybankCreditData> generateCreditList(List<String> items) {
        return items.stream()
                .filter(s -> DateValidator.getInstance().isValid(s, "dd/MM"))
                .map(s -> MaybankCreditData.builder()
                        .statementDate(statementDate)
                        .paymentDueDate(paymentDueDate)
                        .postingDate(s).build())
                .collect(Collectors.toList());
    }

    private List<String> getItems(RectangularTextContainer<TextChunk> content) {
        return content.getTextElements().stream()
                .map(TextChunk::getText)
                .collect(Collectors.toList());
    }

    private List<String> getDescriptionItems(RectangularTextContainer<TextChunk> content){
        String item = content.getText(true).replace("\r", TEXT_SPLITTER);
        String[] split = item.split(TEXT_SPLITTER);
        return Arrays.asList(split);
    }

    private void updateCredits(List<MaybankCreditData> credits, List<String> data, BiConsumer<MaybankCreditData, String> setter) {
        for (int p = 0; p < credits.size(); p++) {
            MaybankCreditData creditData = credits.get(p);
            setter.accept(creditData, data.get(p));
        }
    }
}
