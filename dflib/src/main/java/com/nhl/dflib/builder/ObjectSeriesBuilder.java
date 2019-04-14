package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.MutableList;

/**
 * @param <V>
 * @since 0.6
 */
public class ObjectSeriesBuilder<V> implements SeriesBuilder<V, V> {

    private MutableList<V> accummulator;

    public ObjectSeriesBuilder() {
        this.accummulator = new MutableList<>();
    }

    @Override
    public void append(V v) {
        accummulator.add(v);
    }

    @Override
    public Series<V> toSeries() {
        return accummulator.toSeries();
    }
}
