package com.nhl.dflib.csv;

import com.nhl.dflib.Index;
import com.nhl.dflib.csv.loader.CsvSeriesAppender;
import com.nhl.dflib.csv.loader.CsvCell;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Predicate;

class FilteringSamplingCsvLoaderWorker extends SamplingCsvLoaderWorker {

    private CsvCell<?>[] csvRow;
    private Predicate<CsvCell<?>[]> csvRowFilter;

    public FilteringSamplingCsvLoaderWorker(
            Index columnIndex,
            CsvSeriesAppender<?>[] columns,
            CsvCell<?>[] csvRow,
            Predicate<CsvCell<?>[]> csvRowFilter,
            int rowSampleSize,
            Random rowsSampleRandom) {

        super(columnIndex, columns, rowSampleSize, rowsSampleRandom);
        this.csvRow = csvRow;
        this.csvRowFilter = csvRowFilter;
    }

    @Override
    protected void consumeCSV(Iterator<CSVRecord> it) {
        int width = columnIndex.size();

        int i = 0;
        while (it.hasNext()) {

            CSVRecord row = it.next();

            // perform filtering in a separate buffer before sampling....

            // 1. fill the buffer for condition evaluation. All values will be converted to the right data types
            int csvWidth = csvRow.length;
            for (int j = 0; j < csvWidth; j++) {
                csvRow[j].set(row);
            }

            // 2. eval filters
            if (csvRowFilter.test(csvRow)) {

                // 3. sample row since the condition is satisfied
                sampleBufferedRow(i++, width);
            }
        }
    }

    protected void sampleBufferedRow(int rowNumber, int width) {

        // Reservoir sampling algorithm per https://en.wikipedia.org/wiki/Reservoir_sampling

        // fill "reservoir" first
        if (rowNumber < rowSampleSize) {
            addBufferedRow(width);
            sampledRows.pushInt(rowNumber);
        }
        // replace previously filled values based on random sampling with decaying probability
        else {
            int pos = rowsSampleRandom.nextInt(rowNumber + 1);
            if (pos < rowSampleSize) {
                replaceBufferedRow(pos, width);
                sampledRows.replaceInt(pos, rowNumber);
            }
        }
    }

    protected void addBufferedRow(int width) {
        for (int i = 0; i < width; i++) {
            columnBuilders[i].extractConverted(csvRow);
        }
    }

    protected void replaceBufferedRow(int pos, int width) {
        for (int i = 0; i < width; i++) {
            columnBuilders[i].extractConverted(csvRow, pos);
        }
    }
}
