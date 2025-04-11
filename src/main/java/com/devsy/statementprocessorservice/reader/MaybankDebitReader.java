package com.devsy.statementprocessorservice.reader;


import com.devsy.statementprocessorservice.pojo.MaybankDebit;
import com.devsy.statementprocessorservice.pojo.TextChunkCustom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.ClassPathResource;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Slf4j
public class MaybankDebitReader implements StatementReader {

    public static void main (String[] args) {
        try {
            MaybankDebitReader reader = new MaybankDebitReader();
            File file = new ClassPathResource("mypdf.pdf").getFile();
            reader.read(file);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public void read(File file) {
            try {

            PDDocument document = Loader.loadPDF(file);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();
            List<MaybankDebit> allDebits = new ArrayList<>();

            while (pi.hasNext()) {
                Page page = pi.next();
                List<Ruling> rulings = this.getCustomizedRulings(page);
                List<Table> tables = sea.extract(page, rulings);
                Table table = this.getFirstTableOnly(tables);

                if (table == null) continue;

                List<List<RectangularTextContainer>> rows = table.getRows();

                for (int i = 1; i < rows.size(); i++) {
                    List<RectangularTextContainer> cells = rows.get(i);
                    List<MaybankDebit> debits = new ArrayList<>();

                    for (int k = 0; k < cells.size(); k++) {
                        RectangularTextContainer<TextChunk> content = cells.get(k);
                        List<String> items = this.getItems(content);

                        switch (k) {
                            case 0:
                                debits.addAll(this.generateDebitList(items));
                                break;

                            case 1:
                                if (CollectionUtils.isEmpty(debits)) break;
                                List<String> descriptions = this.getTransactionDescriptionsOrAssignPrevious(items, allDebits);
                                for (int p = 0; p < descriptions.size(); p++) {
                                    MaybankDebit debit = debits.get(p);
                                    debit.setDescription(descriptions.get(p));
                                }
                                break;

                            case 2:
                                List<String> filters = this.getTransactionAmounts(items);
                                for (int p = 0; p < filters.size(); p++) {
                                    MaybankDebit debit = debits.get(p);
                                    debit.setAmount(filters.get(p));
                                }
                                break;

                            case 3:
                                List<String> statementBalances = this.getStatementAmounts(items);
                                if (CollectionUtils.size(statementBalances) > CollectionUtils.size(debits)) {
                                    statementBalances.removeFirst();
                                }

                                for (int p = 0; p < statementBalances.size(); p++) {
                                    MaybankDebit debit = debits.get(p);
                                    debit.setStatementBalance(statementBalances.get(p));
                                }
                        }
                    }
                    allDebits.addAll(debits);
                }
                System.out.println("All debits: " + allDebits);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Ruling> getCustomizedRulings(Page page) {
        List<Ruling> rulings = page.getRulings();

        // Additional Rulings: Maybank debit not having vertical line
        Point2D a1 = new Point2D.Float( 23.0F, 187.66667F);
        Point2D a2 = new Point2D.Float( 23.0F, 716.0F);
        rulings.add(new Ruling(a1, a2));

        Point2D b1 = new Point2D.Float( 487.0F, 187.66667F);
        Point2D b2 = new Point2D.Float( 487.0F, 716.0F);
        rulings.add(new Ruling(b1, b2));

        return rulings;
    }

    private Table getFirstTableOnly(List<Table> tables){
        return Optional.ofNullable(tables)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
    }

    private List<String> getItems(RectangularTextContainer<TextChunk> content) {
        return content.getTextElements().stream()
                .map(te -> new TextChunkCustom(te).getText())
                .collect(Collectors.toList());
    }

    private List<MaybankDebit> generateDebitList(List<String> items) {
        return items.stream()
                .map(StringUtils::trim)
                .filter(s -> s.matches("\\d{2}/\\d{2}/\\d{2}"))
                .map(itm -> MaybankDebit.builder().date(itm).build())
                .collect(Collectors.toUnmodifiableList());
    }

    private List<String> getTransactionDescriptionsOrAssignPrevious(List<String> items, List<MaybankDebit> globalDebits) {
        List<String> transactionDescriptions = items.stream().reduce(
                        new ArrayList<String>(),
                        (prevList, nextText) ->  this.accumulatorMergeDescriptions(prevList, nextText, globalDebits),
                        this.combinerMergeDescription);
        return this.filterIrrelevantDescriptions(transactionDescriptions);
    }

    private ArrayList<String> accumulatorMergeDescriptions(List<String> prevList, String nextText, List<MaybankDebit> globalDebits) {
        if (StringUtils.startsWith(nextText, " ")) {
            String item = StringUtils.trim(nextText);
            if (CollectionUtils.isNotEmpty(prevList)) {
                int lastIndex = prevList.size() - 1;
                prevList.set(lastIndex, prevList.get(lastIndex) + " " + item);
            } else {
                if (CollectionUtils.isNotEmpty(globalDebits)) {
                    MaybankDebit debit = globalDebits.getLast();
                    debit.setDescription(debit.getDescription() + " " + item);
                }
            }
        } else {
            prevList.add(nextText);
        }
        return (ArrayList<String>) prevList;
    }

    private final BinaryOperator<ArrayList<String>> combinerMergeDescription = (c, d)->{
        // For sequential streams, the combiner is irrelevant — it’s not used at all.
        c.addAll(d);
        return c;
    };

    private List<String> filterIrrelevantDescriptions(List<String> descriptions) {
        return descriptions.stream()
                .filter(d -> !StringUtils.containsAnyIgnoreCase(d,
                        "BEGINNING BALANCE",
                        "ENDING BALANCE", "TOTAL CREDIT", "TOTAL DEBIT",
                        "ECTED BY PIDM", "AY NOW SWITCH YOUR CONVENTIONAL CURRENT OR SAVIN"))
                .toList();
    }

    private List<String> getTransactionAmounts (List<String> items) {
        return items.stream()
                .filter(s -> s.matches("\\d+(\\.\\d+)?[+-]"))
                .toList();
    }

    private List<String> getStatementAmounts (List<String> items) {
        return items.stream()
                .filter(s -> s.matches("\\d+(\\.\\d+)?"))
                .collect(Collectors.toList());
    }
}
