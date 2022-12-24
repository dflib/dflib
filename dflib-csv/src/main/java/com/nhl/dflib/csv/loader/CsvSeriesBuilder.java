package com.nhl.dflib.csv.loader;

import com.nhl.dflib.builder.SeriesBuilder;
import com.nhl.dflib.builder.ValueExtractor;
import org.apache.commons.csv.CSVRecord;

/**
 * @since 0.16
 */
public class CsvSeriesBuilder<T> extends SeriesBuilder<CSVRecord, T> {

    private final int csvColumnPosition;

    public CsvSeriesBuilder(ValueExtractor<CSVRecord, T> converter, int csvColumnPosition) {
        super(converter);
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
