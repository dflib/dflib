package com.nhl.dflib.csv;

import com.nhl.dflib.Index;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;
import java.util.Random;

class SamplingCsvLoaderWorker extends CsvLoaderWorker {

    private int rowSampleSize;
    private Random rowsSampleRandom;

    SamplingCsvLoaderWorker(Index columns, SeriesBuilder<String, ?>[] accumulators, int rowSampleSize, Random rowsSampleRandom) {
        super(columns, accumulators);
        this.rowSampleSize = rowSampleSize;
        this.rowsSampleRandom = rowsSampleRandom;
    }

    @Override
    protected void addRows(Iterator<CSVRecord> it, int width) {
        int i = 0;
        while (it.hasNext()) {
            addRow(i++, width, it.next());
        }
    }

    protected void addRow(int rowNumber, int width, CSVRecord record) {

        // Reservoir sampling per https://en.wikipedia.org/wiki/Reservoir_sampling

        if (rowNumber < rowSampleSize) {
            addRow(width, record);
        } else {
            int pos = rowsSampleRandom.nextInt(rowNumber + 1);
            if (pos < rowSampleSize) {
                replaceRow(pos, width, record);
            }
        }
    }

    protected void replaceRow(int pos, int width, CSVRecord record) {
        for (int i = 0; i < width; i++) {
            accumulators[i].set(pos, record.get(i));
        }
    }
}
