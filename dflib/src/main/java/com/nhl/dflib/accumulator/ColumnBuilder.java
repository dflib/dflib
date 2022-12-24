package com.nhl.dflib.accumulator;

import com.nhl.dflib.Series;

/**
 * A mutable by-row Series builder that obtains and converts values from some abstract row source. Provides a high-level
 * API over {@link Accumulator} and {@link ValueConverter}.
 *
 * @since 0.16
 */
public class ColumnBuilder<R, T> {

    protected final ValueConverter<R, T> converter;
    protected final Accumulator<T> accumulator;

    public ColumnBuilder(ValueConverter<R, T> converter, Accumulator<T> accumulator) {
        this.converter = converter;
        this.accumulator = accumulator;
    }

    public void convertAndAdd(R row) {
        converter.convertAndStore(row, accumulator);
    }

    public void convertAndReplace(int pos, R row) {
        converter.convertAndStore(pos, row, accumulator);
    }

    public Series<T> toColumn() {
        return accumulator.toSeries();
    }
}
