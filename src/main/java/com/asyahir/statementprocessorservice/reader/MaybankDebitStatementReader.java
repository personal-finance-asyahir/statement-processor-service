package com.asyahir.statementprocessorservice.reader;


import com.asyahir.statementprocessorservice.pojo.MaybankDebitData;
import com.asyahir.statementprocessorservice.pojo.TextChunkCustom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.CurrencyValidator;
import org.apache.commons.validator.routines.DateValidator;
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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Slf4j
public class MaybankDebitStatementReader implements StatementReader<MaybankDebitData> {

    public static void main(String[] args) {
        try {
            MaybankDebitStatementReader reader = new MaybankDebitStatementReader();
            File file = new ClassPathResource("mypdf.pdf").getFile();
            reader.read(file);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private final List<MaybankDebitData> allDebits = new ArrayList<>();

    @Override
    public List<MaybankDebitData> read(File file) {
        try {
            PDDocument document = Loader.loadPDF(file);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();
            while (pi.hasNext()) {
                Page page = pi.next();
                List<Ruling> rulings = this.getCustomizedRulings(page);
                List<Table> tables = sea.extract(page, rulings);
                Table table = this.getFirstTableOnly(tables);

                if (table == null) continue;

                List<List<RectangularTextContainer>> rows = table.getRows();

                for (int i = 1; i < rows.size(); i++) {
                    List<RectangularTextContainer> cells = rows.get(i);
                    List<MaybankDebitData> debits = new ArrayList<>();

                    for (int k = 0; k < cells.size(); k++) {
                        RectangularTextContainer<TextChunk> content = cells.get(k);
                        List<String> items = this.getItems(content);

                        switch (k) {
                            case 0: // Date
                                debits.addAll(this.generateDebitList(items));
                                break;

                            case 1: // Transaction Description
                                if (CollectionUtils.isEmpty(debits)) break;
                                List<String> descriptions = this.getTransactionDescriptionsOrAssignPrevious(items, allDebits);
                                this.updateDebits(debits, descriptions, MaybankDebitData::setDescription);
                                break;

                            case 2: // Transaction Amount
                                List<String> amounts = this.getTransactionAmounts(items);
                                this.updateDebits(debits, amounts, MaybankDebitData::setAmount);
                                break;

                            case 3: // Statement Balance
                                List<String> statementBalances = this.getStatementAmounts(items);
                                if (CollectionUtils.size(statementBalances) > CollectionUtils.size(debits)) {
                                    statementBalances.removeFirst();
                                }
                                this.updateDebits(debits, statementBalances, MaybankDebitData::setStatementBalance);
                        }
                    }
                    allDebits.addAll(debits);
                }
            }
            return allDebits;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Ruling> getCustomizedRulings(Page page) {
        List<Ruling> rulings = page.getRulings();

        // Additional Rulings: Maybank debit not having vertical line
        Point2D a1 = new Point2D.Float(23.0F, 187.66667F);
        Point2D a2 = new Point2D.Float(23.0F, 716.0F);
        rulings.add(new Ruling(a1, a2));

        Point2D b1 = new Point2D.Float(487.0F, 187.66667F);
        Point2D b2 = new Point2D.Float(487.0F, 716.0F);
        rulings.add(new Ruling(b1, b2));

        return rulings;
    }

    private Table getFirstTableOnly(List<Table> tables) {
        return Optional.ofNullable(tables).stream()
                .flatMap(Collection::stream)
                .findFirst().orElse(null);
    }

    private List<String> getItems(RectangularTextContainer<TextChunk> content) {
        return content.getTextElements().stream()
                .map(te -> new TextChunkCustom(te).getText())
                .collect(Collectors.toList());
    }

    private List<MaybankDebitData> generateDebitList(List<String> items) {
        return items.stream().map(StringUtils::trim)
                .filter(s -> DateValidator.getInstance().isValid(s, "dd/MM/yy"))
                .map(s -> MaybankDebitData.builder().date(s).build())
                .collect(Collectors.toUnmodifiableList());
    }

    private List<String> getTransactionDescriptionsOrAssignPrevious(List<String> items, List<MaybankDebitData> globalDebits) {
        List<String> transactionDescriptions = items.stream().reduce(new ArrayList<>(),
                this.accumulatorMergeDescriptions,
                this.combinerMergeDescription);
        return this.filterIrrelevantDescriptions(transactionDescriptions);
    }

    private final BiFunction<ArrayList<String>, String, ArrayList<String>> accumulatorMergeDescriptions = (previousList, nextText) -> {
        if (StringUtils.startsWith(nextText, " ")) {
            String item = StringUtils.trim(nextText);
            if (CollectionUtils.isNotEmpty(previousList)) {
                int lastIndex = CollectionUtils.size(previousList) - 1;
                String mergeDescription = previousList.get(lastIndex)
                        + System.lineSeparator()
                        + item;
                previousList.set(lastIndex, mergeDescription);
            } else {
                if (CollectionUtils.isNotEmpty(this.allDebits)) {
                    MaybankDebitData debit = this.allDebits.getLast();
                    debit.setDescription(debit.getDescription() + " " + item);
                }
            }
        } else {
            previousList.add(nextText);
        }
        return (ArrayList<String>) previousList;
    };

    private final BinaryOperator<ArrayList<String>> combinerMergeDescription = (c, d) -> {
        // For sequential streams, the combiner is irrelevant — it’s not used at all.
        c.addAll(d);
        return c;
    };

    private List<String> filterIrrelevantDescriptions(List<String> descriptions) {
        return descriptions.stream()
                .filter(d -> !StringUtils.containsAnyIgnoreCase(d, "BEGINNING BALANCE",
                        "ENDING BALANCE",
                        "TOTAL CREDIT",
                        "TOTAL DEBIT",
                        "ECTED BY PIDM",
                        "AY NOW SWITCH YOUR CONVENTIONAL CURRENT OR SAVIN"))
                .toList();
    }

    private List<String> getTransactionAmounts(List<String> items) {
        return items.stream()
                .filter(s -> StringUtils.indexOfAny(s, "+", "-") > -1)
                .filter(s -> {
                    int operationIndex = StringUtils.length(s) - 1;
                    String number = StringUtils.left(s, operationIndex);
                    return CurrencyValidator.getInstance().isValid(number);
                }).collect(Collectors.toList());
    }

    private List<String> getStatementAmounts(List<String> items) {
        return items.stream()
                .filter(s -> CurrencyValidator.getInstance().isValid(s))
                .collect(Collectors.toList());
    }

    private void updateDebits(List<MaybankDebitData> debits, List<String> data, BiConsumer<MaybankDebitData, String> setter) {
        for (int p = 0; p < data.size(); p++) {
            MaybankDebitData debit = debits.get(p);
            setter.accept(debit, data.get(p));
        }
    }
}
