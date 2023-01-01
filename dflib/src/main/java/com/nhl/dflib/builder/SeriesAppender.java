package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.Extractor;

/**
 * A mutable by-row Series builder that obtains and converts values from some abstract row source. Provides a high-level
 * API over {@link ValueAccum} and {@link Extractor}.
 *
 * @since 0.16
 */
public class SeriesAppender<S, T> {

    protected final Extractor<S, T> extractor;
    protected final ValueAccum<T> accum;

    public SeriesAppender(Extractor<S, T> extractor, int accumCapacity) {
        this.extractor = extractor;
        this.accum = extractor.createAccum(accumCapacity);
    }

    public void append(S from) {
        extractor.extractAndStore(from, accum);
    }

    public void replace(S from, int toPos) {
        extractor.extractAndStore(from, accum, toPos);
    }

    public Series<T> toSeries() {
        return accum.toSeries();
    }
}
