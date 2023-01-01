package com.nhl.dflib.csv.loader;

import com.nhl.dflib.builder.SeriesAppender;
import com.nhl.dflib.Extractor;
import org.apache.commons.csv.CSVRecord;

/**
 * @since 0.16
 */
public class CsvSeriesAppender<T> extends SeriesAppender<CSVRecord, T> {

    private final int csvColumnPosition;

    public CsvSeriesAppender(Extractor<CSVRecord, T> converter, int csvColumnPosition) {
        // TODO: be more precise about capacity .. e.g. when sampling we can use the sample size as capacity
        super(converter, 10);
        this.csvColumnPosition = csvColumnPosition;
    }

    public void extractConverted(CsvCell<?>[] from) {
        // values are already converted, so bypassing the converter
        CsvCell cell = from[csvColumnPosition];
        cell.store(accum);
    }

    public void extractConverted(CsvCell<?>[] from, int toPos) {
        // values are already converted, so bypassing the converter
        CsvCell cell = from[csvColumnPosition];
        cell.store(toPos, accum);
    }
}
