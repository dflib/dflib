package com.nhl.dflib.csv.loader;

import com.nhl.dflib.builder.SeriesBuilder;
import com.nhl.dflib.Extractor;
import org.apache.commons.csv.CSVRecord;

/**
 * @since 0.16
 */
public class CsvSeriesBuilder<T> extends SeriesBuilder<CSVRecord, T> {

    private final int csvColumnPosition;

    public CsvSeriesBuilder(Extractor<CSVRecord, T> converter, int csvColumnPosition) {
        // TODO: be more precise about capacity .. e.g. when sampling we can use the sample size as capacity
        super(converter, 10);
        this.csvColumnPosition = csvColumnPosition;
    }

    public void extractConverted(CsvCell<?>[] from) {
        // values are already converted, so bypassing the converter
        CsvCell cell = from[csvColumnPosition];
        cell.store(accumulator);
    }

    public void extractConverted(CsvCell<?>[] from, int toPos) {
        // values are already converted, so bypassing the converter
        CsvCell cell = from[csvColumnPosition];
        cell.store(toPos, accumulator);
    }
}
