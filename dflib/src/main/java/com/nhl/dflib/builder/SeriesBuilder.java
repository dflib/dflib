package com.nhl.dflib.builder;

import com.nhl.dflib.Series;

/**
 * @param <I>
 * @param <O>
 * @since 0.6
 */
public interface SeriesBuilder<I, O> {

    void append(I v);

    Series<O> toSeries();
}
