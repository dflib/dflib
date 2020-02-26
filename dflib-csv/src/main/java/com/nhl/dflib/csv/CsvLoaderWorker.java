package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;
import java.util.function.Predicate;

class CsvLoaderWorker {

    protected SeriesBuilder<String, ?>[] accumulators;
    protected Index columns;
    protected int[] csvPositions;
    protected Predicate<SeriesBuilder<String, ?>[]> rowFilter;

    CsvLoaderWorker(
            Index columns,
            int[] csvPositions,
            SeriesBuilder<String, ?>[] accumulators,
            Predicate<SeriesBuilder<String, ?>[]> rowFilter) {

        this.columns = columns;
        this.csvPositions = csvPositions;
        this.accumulators = accumulators;
        this.rowFilter = rowFilter;
    }

    DataFrame load(Iterator<CSVRecord> it) {
        consumeCSV(it);
        return toDataFrame();
    }

    protected void consumeCSV(Iterator<CSVRecord> it) {
        int width = columns.size();
        while (it.hasNext()) {
            addRow(width, it.next());
            filterRow(width);
        }
    }

    protected boolean filterRow(int width) {
        // perform filtering after the row is added to accumulators with proper value conversions
        if (!rowFilter.test(accumulators)) {
            popRow(width);
            return false;
        }

        return true;
    }

    protected DataFrame toDataFrame() {
        int width = columns.size();
        Series<?>[] series = new Series[width];
        for (int i = 0; i < width; i++) {
            series[i] = accumulators[i].toSeries();
        }

        return DataFrame.newFrame(columns).columns(series);
    }

    protected void addRow(int width, CSVRecord row) {
        for (int i = 0; i < width; i++) {
            accumulators[i].add(row.get(csvPositions[i]));
        }
    }

    protected void popRow(int width) {
        for (int i = 0; i < width; i++) {
            accumulators[i].pop();
        }
    }
}
