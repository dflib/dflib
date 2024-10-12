package org.dflib.builder;

import org.dflib.Series;
import org.dflib.Extractor;

/**
 * A mutable by-row Series builder that obtains and converts values from some abstract row source. Provides a high-level
 * API over {@link ValueAccum} and {@link Extractor}.

 */
public class SeriesAppender<S, T> {

    protected final Extractor<S, T> extractor;
    protected final ValueAccum<T> accum;

    public SeriesAppender(Extractor<S, T> extractor, int accumCapacity) {
        this.extractor = extractor;
        this.accum = extractor.createAccum(accumCapacity);
    }

    public SeriesAppender<S, T> append(S from) {
        extractor.extractAndStore(from, accum);
        return this;
    }

    public SeriesAppender<S, T> append(Iterable<S> from) {
        for (S s : from) {
            append(s);
        }

        return this;
    }

    public void replace(S from, int toPos) {
        extractor.extractAndStore(from, accum, toPos);
    }

    public int size() {
        return accum.size();
    }

    public Series<T> toSeries() {
        return accum.toSeries();
    }
}
