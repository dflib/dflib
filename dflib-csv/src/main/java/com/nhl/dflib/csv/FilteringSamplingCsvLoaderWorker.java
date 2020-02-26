package com.nhl.dflib.csv;

import com.nhl.dflib.Index;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Predicate;

class FilteringSamplingCsvLoaderWorker extends SamplingCsvLoaderWorker {

    private SeriesBuilder<String, ?>[] presampleAccummulators;
    private Predicate<SeriesBuilder<String, ?>[]> rowFilter;

    public FilteringSamplingCsvLoaderWorker(
            Index columns,
            int[] csvPositions,
            SeriesBuilder<String, ?>[] accumulators,
            SeriesBuilder<String, ?>[] presampleAccummulators,
            Predicate<SeriesBuilder<String, ?>[]> rowFilter,
            int rowSampleSize,
            Random rowsSampleRandom) {

        super(columns, csvPositions, accumulators, rowSampleSize, rowsSampleRandom);

        this.presampleAccummulators = presampleAccummulators;
        this.rowFilter = rowFilter;
    }

    protected void consumeCSV(Iterator<CSVRecord> it) {
        int width = columns.size();

        int i = 0;
        while (it.hasNext()) {

            CSVRecord row = it.next();

            // perform filtering before sampling and use a separate buffer ..
            // TODO: the inefficiency here is double data conversion for every sampled row:
            //  once in pre-sample, second time - during sampling, as conversion happens inside accumulator

            for (int j = 0; j < width; j++) {
                presampleAccummulators[j].add(row.get(csvPositions[j]));
            }

            if (rowFilter.test(presampleAccummulators)) {
                sampleRow(i++, width, row);
            }

            for (int j = 0; j < width; j++) {
                presampleAccummulators[j].pop();
            }
        }
    }
}
