package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.Extractor;

/**
 * A mutable by-row Series builder that obtains and converts values from some abstract row source. Provides a high-level
 * API over {@link ValueAccum} and {@link Extractor}.
 *
 * @since 0.16
 */
public class SeriesBuilder<F, T> {

    protected final Extractor<F, T> extractor;
    protected final ValueAccum<T> accumulator;

    public SeriesBuilder(Extractor<F, T> extractor, int accumCapacity) {
        this.extractor = extractor;
        this.accumulator = extractor.createAccum(accumCapacity);
    }

    public int accumulatedSize() {
        return accumulator.size();
    }

    public void extractAndStore(F from) {
        extractor.extractAndStore(from, accumulator);
    }

    public void extractAndStore(F from, int toPos) {
        extractor.extractAndStore(from, accumulator, toPos);
    }

    public Series<T> toSeries() {
        return accumulator.toSeries();
    }
}
