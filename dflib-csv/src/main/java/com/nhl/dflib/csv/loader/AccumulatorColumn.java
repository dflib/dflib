package com.nhl.dflib.csv.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.Accumulator;
import com.nhl.dflib.series.builder.ValueConverter;
import org.apache.commons.csv.CSVRecord;

/**
 * @since 0.8
 */
public class AccumulatorColumn<T> {

    private ValueConverter<String, T> converter;
    private Accumulator<T> accumulator;
    private int csvColumnPosition;

    public AccumulatorColumn(ValueConverter<String, T> converter, Accumulator<T> accumulator, int csvColumnPosition) {
        this.converter = converter;
        this.accumulator = accumulator;
        this.csvColumnPosition = csvColumnPosition;
    }

    public void add(CSVRecord record) {
        converter.convertAndStore(record.get(csvColumnPosition), accumulator);
    }

    public void add(ValueHolderColumn<?>[] values) {
        // values are already converted, so bypassing the converter
        ValueHolderColumn vhColumn = values[csvColumnPosition];
        vhColumn.store(accumulator);
    }

    public void set(int pos, CSVRecord record) {
        converter.convertAndStore(pos, record.get(csvColumnPosition), accumulator);
    }

    public void set(int pos, ValueHolderColumn<?>[] values) {
        // values are already converted, so bypassing the converter
        ValueHolderColumn vhColumn = values[csvColumnPosition];
        vhColumn.store(pos, accumulator);
    }

    public Series<T> toSeries() {
        return accumulator.toSeries();
    }
}
