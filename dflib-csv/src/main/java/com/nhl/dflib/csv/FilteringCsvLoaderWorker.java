package com.nhl.dflib.csv;

import com.nhl.dflib.Index;
import com.nhl.dflib.csv.loader.CsvAccumulatorColumn;
import com.nhl.dflib.csv.loader.CsvValueHolderColumn;
import org.apache.commons.csv.CSVRecord;

import java.util.function.Predicate;

class FilteringCsvLoaderWorker extends BaseCsvLoaderWorker {

    private CsvValueHolderColumn<?>[] csvColumns;
    private Predicate<CsvValueHolderColumn<?>[]> rowFilter;

    public FilteringCsvLoaderWorker(
            Index columnIndex,
            CsvAccumulatorColumn<?>[] columns,
            CsvValueHolderColumn<?>[] csvColumns,
            Predicate<CsvValueHolderColumn<?>[]> rowFilter) {

        super(columnIndex, columns);

        this.csvColumns = csvColumns;
        this.rowFilter = rowFilter;
    }

    @Override
    protected void addRow(int width, CSVRecord row) {

        // 1. fill the buffer for condition evaluation. The values will be converted to the right data types
        int csvWidth = csvColumns.length;
        for (int i = 0; i < csvWidth; i++) {
            csvColumns[i].set(row);
        }

        // 2. eval filters
        if (rowFilter.test(csvColumns)) {

            // 3. add row since the condition is satisfied
            for (int i = 0; i < width; i++) {
                columns[i].add(csvColumns);
            }

        }
    }
}
