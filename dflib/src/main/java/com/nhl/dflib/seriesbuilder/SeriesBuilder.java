package com.nhl.dflib.seriesbuilder;

import com.nhl.dflib.Series;

/**
 * @param <I>
 * @param <O>
 * @since 0.6
 */
public interface SeriesBuilder<I, O> {

    void add(I v);

    Series<O> toSeries();
}
