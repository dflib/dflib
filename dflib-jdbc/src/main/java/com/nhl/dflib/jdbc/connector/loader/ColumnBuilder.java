package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ValueConverter;

import java.sql.ResultSet;

/**
 * A mutable accumulator of column values for the DataFrame built from JDBC ResultSet.
 *
 * @since 0.8
 */
public class ColumnBuilder<T> {

    private ValueConverter<ResultSet, T> converter;
    private Accumulator<T> accumulator;

    public ColumnBuilder(ValueConverter<ResultSet, T> converter, Accumulator<T> accumulator) {
        this.converter = converter;
        this.accumulator = accumulator;
    }

    public void add(ResultSet rs) {
        converter.convertAndStore(rs, accumulator);
    }

    public void set(int pos, ResultSet rs) {
        converter.convertAndStore(pos, rs, accumulator);
    }

    public Series<T> toColumn() {
        return accumulator.toSeries();
    }
}
