package com.asyahir.statementprocessorservice.reader;

import com.asyahir.statementprocessorservice.pojo.MaybankCreditData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

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

    public List<MaybankCreditData> read() {

        try {
            PDDocument document = Loader.loadPDF(this.file);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();
            while (pi.hasNext()) {
                Page page = pi.next();
                boolean isFirstPage = page.getPageNumber() == 1;

                List<Table> tables = sea.extract(page);

                if (CollectionUtils.size(tables) <3) return List.of();

                Table table = tables.get(2);

                if (isFirstPage)  {
                    this.extractStatementDate(table);
                    table = tables.getLast();
                }

                List<List<RectangularTextContainer>> rows = this.getTrimmedRows(table);

                for (List<RectangularTextContainer> cells : rows) {
                    List<MaybankCreditData> credits = new ArrayList<>();
                    for (int k = 0; k < cells.size(); k++) {
                        RectangularTextContainer<TextChunk> content = cells.get(k);

                        List<String> items = this.getItems(content);

                        if (isFirstPage) items.removeFirst();

                        switch(k) {
                            case 0: // Posting Date
                                credits.addAll(this.generateCreditList(items));
                                break;
                            case 1: // Transaction Date
                                this.updateCredits(credits, items, MaybankCreditData::setDate);
                                break;
                            case 2: // Transaction Description
                                items = this.getDescriptionItems(content);
                                if (isFirstPage) {
                                    items = items.subList(7, CollectionUtils.size(items));
                                }
                                this.updateCredits(credits, items, MaybankCreditData::setDescription);
                                break;
                            case 3: // Amount
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

    private void extractStatementDate(Table table){
        List<List<RectangularTextContainer>> rows = this.getTrimmedRows(table);
        for (List<RectangularTextContainer> cells : rows) {
            for (int i = 0; i < cells.size(); i++) {
                RectangularTextContainer<TextChunk> content = cells.get(i);
                String text = content.getText().replace("\r", "");
                if (i==0) {
                    this.statementDate = StringUtils.trim(text);
                } else if (i==1) {
                    this.paymentDueDate = StringUtils.trim(text);
                }
            }
        }
    }

    private List<List<RectangularTextContainer>> getTrimmedRows (Table table){
        List<List<RectangularTextContainer>> rows = table.getRows();
        if (CollectionUtils.isNotEmpty(rows)){
            rows.removeFirst();
        }
        return rows;
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
