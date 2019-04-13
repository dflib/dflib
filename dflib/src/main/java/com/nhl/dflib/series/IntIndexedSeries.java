package com.nhl.dflib.series;

import com.nhl.dflib.Printers;
import com.nhl.dflib.Series;

import java.util.Objects;

/**
 * @param <T> type of series value
 * @since 0.6
 */
public class IntIndexedSeries<T> implements Series<T> {

    private Series<T> source;
    private IntSeries includePositions;

    private Series<T> materialized;

    public IntIndexedSeries(Series<T> source, IntSeries includePositions) {
        this.source = Objects.requireNonNull(source);
        this.includePositions = Objects.requireNonNull(includePositions);
    }

    @Override
    public int size() {
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

        // reset source reference, allowing to free up memory..
        source = null;
        includePositions = null;

        return new ArraySeries(data);
    }

    @Override
    public Series<T> fillNulls(T value) {
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        return materialize().fillNullsForward();
    }

    @Override
    public String toString() {
        return Printers.inline.print(new StringBuilder("IndexedSeries ["), this).append("]").toString();
    }
}
