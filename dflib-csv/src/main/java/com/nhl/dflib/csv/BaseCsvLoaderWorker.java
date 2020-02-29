package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.csv.loader.AccumulatorColumn;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;

class BaseCsvLoaderWorker implements CsvLoaderWorker {

    protected AccumulatorColumn<?>[] columnAccumulators;
    protected Index columnIndex;

    BaseCsvLoaderWorker(Index columnIndex, AccumulatorColumn<?>[] columnAccumulators) {
        this.columnIndex = columnIndex;
        this.columnAccumulators = columnAccumulators;
    }

    @Override
    public DataFrame load(Iterator<CSVRecord> it) {
        consumeCSV(it);
        return toDataFrame();
    }

    protected void consumeCSV(Iterator<CSVRecord> it) {
        int width = columnIndex.size();
        while (it.hasNext()) {
            addRow(width, it.next());
        }
    }

    protected DataFrame toDataFrame() {
        int width = columnIndex.size();
        Series<?>[] series = new Series[width];
        for (int i = 0; i < width; i++) {
            series[i] = columnAccumulators[i].toSeries();
        }

        return DataFrame.newFrame(columnIndex).columns(series);
    }

    protected void addRow(int width, CSVRecord row) {
        for (int i = 0; i < width; i++) {
            columnAccumulators[i].add(row);
        }
    }
}
