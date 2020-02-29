package com.nhl.dflib.csv;

import com.nhl.dflib.Index;
import com.nhl.dflib.csv.loader.AccumulatorColumn;
import com.nhl.dflib.csv.loader.ValueHolderColumn;
import org.apache.commons.csv.CSVRecord;

import java.util.function.Predicate;

class FilteringCsvLoaderWorker extends BaseCsvLoaderWorker {

    private ValueHolderColumn<?>[] csvRowHolder;
    private Predicate<ValueHolderColumn<?>[]> csvRowFilter;

    public FilteringCsvLoaderWorker(
            Index columnIndex,
            AccumulatorColumn<?>[] columns,
            ValueHolderColumn<?>[] csvRowHolder,
            Predicate<ValueHolderColumn<?>[]> csvRowFilter) {

        super(columnIndex, columns);

        this.csvRowHolder = csvRowHolder;
        this.csvRowFilter = csvRowFilter;
    }

    @Override
    protected void addRow(int width, CSVRecord row) {

        // 1. fill the buffer for condition evaluation. All values will be converted to the right data types
        int csvWidth = csvRowHolder.length;
        for (int i = 0; i < csvWidth; i++) {
            csvRowHolder[i].set(row);
        }

        // 2. eval filters
        if (csvRowFilter.test(csvRowHolder)) {

            // 3. add row since the condition is satisfied
            for (int i = 0; i < width; i++) {
                columnAccumulators[i].add(csvRowHolder);
            }

        }
    }
}
