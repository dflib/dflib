package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.series.builder.IntAccumulator;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;
import java.util.Random;

/**
 * Loads a row sample from a potentially large CSV row iterator, with the specified sample size.
 */
class SamplingCsvLoaderWorker extends CsvLoaderWorker {

    private int rowSampleSize;
    private Random rowsSampleRandom;
    private IntAccumulator sampledRows;

    SamplingCsvLoaderWorker(Index columns, int[] csvPositions, SeriesBuilder<String, ?>[] accumulators, int rowSampleSize, Random rowsSampleRandom) {
        super(columns, csvPositions, accumulators);
        this.rowSampleSize = rowSampleSize;
        this.rowsSampleRandom = rowsSampleRandom;
        this.sampledRows = new IntAccumulator();
    }

    @Override
    protected DataFrame toDataFrame() {
        return sortSampled(super.toDataFrame());
    }

    protected DataFrame sortSampled(DataFrame sampledUnsorted) {
        IntSeries sortIndex = sampledRows.toIntSeries().sortIndexInt();
        return sampledUnsorted.selectRows(sortIndex);
    }

    @Override
    protected void consumeCSV(Iterator<CSVRecord> it) {
        int width = columns.size();
        int i = 0;
        while (it.hasNext()) {
            sampleRow(i++, width, it.next());
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

    protected void replaceRow(int pos, int width, CSVRecord record) {
        for (int i = 0; i < width; i++) {
            accumulators[i].set(pos, record.get(csvPositions[i]));
        }
    }
}
