package com.nhl.dflib.csv;

import com.nhl.dflib.Index;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVRecord;

import java.util.function.Predicate;

class FilteringCsvLoaderWorker extends BaseCsvLoaderWorker {

    private Predicate<SeriesBuilder<String, ?>[]> rowFilter;

    FilteringCsvLoaderWorker(Index columns, int[] csvPositions, SeriesBuilder<String, ?>[] accumulators, Predicate<SeriesBuilder<String, ?>[]> rowFilter) {
        super(columns, csvPositions, accumulators);
        this.rowFilter = rowFilter;
    }

    private void filterRow(int width) {
        // perform filtering after the row is added to accumulators with proper value conversions
        if (!rowFilter.test(accumulators)) {
            popRow(width);
        }
    }

    @Override
    protected void addRow(int width, CSVRecord row) {
        super.addRow(width, row);
        filterRow(width);
    }
}
