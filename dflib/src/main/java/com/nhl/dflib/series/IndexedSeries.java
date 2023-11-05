package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;

import java.util.Objects;

/**
 * @param <T> type of series value
 */
public class IndexedSeries<T> extends ObjectSeries<T> {

    private volatile Series<T> source;
    private volatile IntSeries includePositions;
    private volatile Series<T> materialized;

    public IndexedSeries(Series<T> source, IntSeries includePositions) {
        super(source.getNominalType());
        this.source = Objects.requireNonNull(source);
        this.includePositions = Objects.requireNonNull(includePositions);
    }

    @Override
    public int size() {
        IntSeries includePositions = this.includePositions;
        return includePositions != null ? includePositions.size() : materialized.size();
    }

    @Override
    public T get(int index) {
        return materialize().get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        materialize().copyTo(to, fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterialize();

                    // reset source reference, allowing to free up memory
                    source = null;
                    includePositions = null;
                }
            }
        }

        return materialized;
    }

    protected ArraySeries doMaterialize() {

        int h = includePositions.size();

        Object[] data = new Object[h];

        for (int i = 0; i < h; i++) {
            int index = includePositions.getInt(i);

            // skipped positions (index < 0) are found in joins
            data[i] = index < 0 ? null : source.get(index);
        }

        return new ArraySeries(data);
    }

    @Override
    public Series<T> fillNulls(T value) {
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        return materialize().fillNullsFromSeries(values);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        return materialize().fillNullsForward();
    }
}
