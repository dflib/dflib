package com.nhl.dflib.csv.loader;

import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ColumnBuilder;
import com.nhl.dflib.accumulator.ValueConverter;
import org.apache.commons.csv.CSVRecord;

/**
 * @since 0.16
 */
public class CsvColumnBuilder<T> extends ColumnBuilder<CSVRecord, T> {

    private final int csvColumnPosition;

    public CsvColumnBuilder(ValueConverter<CSVRecord, T> converter, Accumulator<T> accumulator, int csvColumnPosition) {
        super(converter, accumulator);
        this.csvColumnPosition = csvColumnPosition;
    }

    public void add(CsvCell<?>[] values) {
        // values are already converted, so bypassing the converter
        CsvCell vhColumn = values[csvColumnPosition];
        vhColumn.store(accumulator);
    }

    public void replace(int pos, CsvCell<?>[] values) {
        // values are already converted, so bypassing the converter
        CsvCell vhColumn = values[csvColumnPosition];
        vhColumn.store(pos, accumulator);
    }
}
