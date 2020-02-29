package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.Series;

/**
 * @param <I>
 * @param <O>
 * @since 0.8
 */
public interface SeriesBuilder<I, O> {

    void add(I v);

    void set(int pos, I v);

    Series<O> toSeries();
}
