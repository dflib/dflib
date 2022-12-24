package com.nhl.dflib.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.ValueAccum;

/**
 * A mutable by-row Series builder that obtains and converts values from some abstract row source. Provides a high-level
 * API over {@link ValueAccum} and {@link ValueExtractor}.
 *
 * @since 0.16
 */
public class SeriesBuilder<F, T> {

    protected final ValueExtractor<F, T> converter;
    protected final ValueAccum<T> accumulator;

    public SeriesBuilder(ValueExtractor<F, T> converter) {
        this.converter = converter;
        this.accumulator = converter.createAccumulator();
    }

    public void extract(F from) {
        converter.extract(from, accumulator);
    }

    public void extract(F from, int toPos) {
        converter.extract(from, accumulator, toPos);
    }

    public Series<T> toSeries() {
        return accumulator.toSeries();
    }
}
