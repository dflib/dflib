package com.nhl.dflib.csv;

import com.nhl.dflib.Index;
import com.nhl.dflib.csv.loader.CsvColumnBuilder;
import com.nhl.dflib.csv.loader.CsvCell;
import org.apache.commons.csv.CSVRecord;

import java.util.function.Predicate;

class FilteringCsvLoaderWorker extends BaseCsvLoaderWorker {

    private CsvCell<?>[] csvRow;
    private Predicate<CsvCell<?>[]> csvRowFilter;

    public FilteringCsvLoaderWorker(
            Index columnIndex,
            CsvColumnBuilder<?>[] columnBuilders,
            CsvCell<?>[] csvRow,
            Predicate<CsvCell<?>[]> csvRowFilter) {

        super(columnIndex, columnBuilders);

        this.csvRow = csvRow;
        this.csvRowFilter = csvRowFilter;
    }

    @Override
    protected void addRow(int width, CSVRecord row) {

        // 1. fill the buffer for condition evaluation. All values will be converted to the right data types
        int csvWidth = csvRow.length;
        for (int i = 0; i < csvWidth; i++) {
            csvRow[i].set(row);
        }

        // 2. eval filters
        if (csvRowFilter.test(csvRow)) {

            // 3. add row since the condition is satisfied
            for (int i = 0; i < width; i++) {
                columnBuilders[i].add(csvRow);
            }

        }
    }
}
