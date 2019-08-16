package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;

class CsvLoaderWorker {

    private Index columns;
    protected SeriesBuilder<String, ?>[] accumulators;

    public CsvLoaderWorker(Index columns, SeriesBuilder<String, ?>[] accumulators) {
        this.columns = columns;
        this.accumulators = accumulators;
    }

    DataFrame load(Iterator<CSVRecord> it) {

        int width = columns.size();

        addRows(it, width);

        Series<?>[] series = new Series[width];
        for (int i = 0; i < width; i++) {
            series[i] = accumulators[i].toSeries();
        }

        return DataFrame.newFrame(columns).columns(series);
    }

    protected void addRows(Iterator<CSVRecord> it, int width) {
        while (it.hasNext()) {
            addRow(width, it.next());
        }
    }

    protected void addRow(int width, CSVRecord record) {
        for (int i = 0; i < width; i++) {
            accumulators[i].add(record.get(i));
        }
    }

}
