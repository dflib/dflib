package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;

class CsvLoaderWorker {

    protected SeriesBuilder<String, ?>[] accumulators;
    protected Index columns;

    CsvLoaderWorker(Index columns, SeriesBuilder<String, ?>[] accumulators) {
        this.columns = columns;
        this.accumulators = accumulators;
    }

    DataFrame load(Iterator<CSVRecord> it) {
        consumeCSV(it);
        return toDataFrame();
    }

    protected void consumeCSV(Iterator<CSVRecord> it) {
        int width = columns.size();
        while (it.hasNext()) {
            addRow(width, it.next());
        }
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
            accumulators[i].add(row.get(i));
        }
    }
}
