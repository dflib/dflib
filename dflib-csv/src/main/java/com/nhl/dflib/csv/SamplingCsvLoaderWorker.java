package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.IntAccumulator;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;
import java.util.Random;

/**
 * Loads a row sample from a potentially large CSV row iterator, with the specified sample size.
 */
class SamplingCsvLoaderWorker implements CsvLoaderWorker {

    protected SeriesBuilder<String, ?>[] accumulators;
    protected Index columns;
    protected int[] csvPositions;
    private int rowSampleSize;
    private Random rowsSampleRandom;
    private IntAccumulator sampledRows;


    SamplingCsvLoaderWorker(
            Index columns,
            int[] csvPositions,
            SeriesBuilder<String, ?>[] accumulators,
            int rowSampleSize,
            Random rowsSampleRandom) {

        this.columns = columns;
        this.csvPositions = csvPositions;
        this.accumulators = accumulators;
        this.rowSampleSize = rowSampleSize;
        this.rowsSampleRandom = rowsSampleRandom;
        this.sampledRows = new IntAccumulator();
    }

    @Override
    public DataFrame load(Iterator<CSVRecord> it) {
        consumeCSV(it);
        return toDataFrame();
    }

    protected DataFrame toDataFrame() {
        return sortSampled(toUnsortedDataFrame());
    }

    protected DataFrame toUnsortedDataFrame() {
        int width = columns.size();
        Series<?>[] series = new Series[width];
        for (int i = 0; i < width; i++) {
            series[i] = accumulators[i].toSeries();
        }

        return DataFrame.newFrame(columns).columns(series);
    }

    protected DataFrame sortSampled(DataFrame sampledUnsorted) {
        IntSeries sortIndex = sampledRows.toIntSeries().sortIndexInt();
        return sampledUnsorted.selectRows(sortIndex);
    }

    protected void consumeCSV(Iterator<CSVRecord> it) {
        int width = columns.size();
        int i = 0;
        while (it.hasNext()) {
            CSVRecord row = it.next();
            sampleRow(i++, width, row);
        }
    }

    protected void sampleRow(int rowNumber, int width, CSVRecord row) {

        // Reservoir sampling algorithm per https://en.wikipedia.org/wiki/Reservoir_sampling

        // fill "reservoir" first
        if (rowNumber < rowSampleSize) {
            addRow(width, row);
            sampledRows.add(rowNumber);
        }
        // replace previously filled values based on random sampling with decaying probability
        else {
            int pos = rowsSampleRandom.nextInt(rowNumber + 1);
            if (pos < rowSampleSize) {
                replaceRow(pos, width, row);
                sampledRows.set(pos, rowNumber);
            }
        }
    }

    protected void addRow(int width, CSVRecord row) {
        for (int i = 0; i < width; i++) {
            accumulators[i].add(row.get(csvPositions[i]));
        }
    }

    protected void replaceRow(int pos, int width, CSVRecord record) {
        for (int i = 0; i < width; i++) {
            accumulators[i].set(pos, record.get(csvPositions[i]));
        }
    }
}
