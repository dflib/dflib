package com.nhl.dflib.csv.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ValueConverter;
import org.apache.commons.csv.CSVRecord;

/**
 * A mutable accumulator of column values for the DataFrame built from CSV.
 *
 * @since 0.8
 */
public class ColumnBuilder<T> {

    private final ValueConverter<String, T> converter;
    private final Accumulator<T> accumulator;
    private final int csvColumnPosition;

    public ColumnBuilder(ValueConverter<String, T> converter, Accumulator<T> accumulator, int csvColumnPosition) {
        this.converter = converter;
        this.accumulator = accumulator;
        this.csvColumnPosition = csvColumnPosition;
    }

    public void add(CSVRecord record) {
        converter.convertAndStore(record.get(csvColumnPosition), accumulator);
    }

    public void add(CsvCell<?>[] values) {
        // values are already converted, so bypassing the converter
        CsvCell vhColumn = values[csvColumnPosition];
        vhColumn.store(accumulator);
    }

    public void set(int pos, CSVRecord record) {
        converter.convertAndStore(pos, record.get(csvColumnPosition), accumulator);
    }

    public void set(int pos, CsvCell<?>[] values) {
        // values are already converted, so bypassing the converter
        CsvCell vhColumn = values[csvColumnPosition];
        vhColumn.store(pos, accumulator);
    }

    public Series<T> toColumn() {
        return accumulator.toSeries();
    }
}
