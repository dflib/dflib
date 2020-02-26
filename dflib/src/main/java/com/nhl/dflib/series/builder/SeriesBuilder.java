package com.nhl.dflib.series.builder;

import com.nhl.dflib.Series;

/**
 * @param <I>
 * @param <O>
 * @since 0.6
 */
public interface SeriesBuilder<I, O> {

    void add(I v);

    void set(int pos, I v);

    /**
     * @return the latest appended value.
     * @since 0.8
     */
    O peek();

    /**
     * Resets builder, removing the latest appended value.
     *
     * @since 0.8
     */
    void pop();

    Series<O> toSeries();
}
