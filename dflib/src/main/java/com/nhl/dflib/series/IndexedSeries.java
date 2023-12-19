package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;

import java.util.Objects;

/**
 * @param <T> type of series value
 */
public class IndexedSeries<T> extends ObjectSeries<T> {

    protected volatile Raw<T> raw;
    protected volatile Series<T> materialized;

    public IndexedSeries(Series<T> source, IntSeries includePositions) {
        super(source.getNominalType());
        this.raw = new Raw<>(source, includePositions);
    }

    /**
     * @since 1.0.0-M19
     */
    public boolean isMaterialized() {
        return materialized != null;
    }

    @Override
    public int size() {
        Raw raw = this.raw;
        return raw != null ? raw.size() : materialized.size();
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
                    materialized = raw.materialize();

                    // reset source reference, allowing to free up memory
                    raw = null;
                }
            }
        }

        return materialized;
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

    static class Raw<T> {
        final Series<T> source;
        final IntSeries includePositions;

        Raw(Series<T> source, IntSeries includePositions) {
            this.source = Objects.requireNonNull(source);
            this.includePositions = Objects.requireNonNull(includePositions);
        }

        int size() {
            return includePositions.size();
        }

        T get(int index) {
            int i = includePositions.getInt(index);

            // skipped positions (index < 0) are found in joins
            return i < 0 ? null : source.get(i);
        }

        ArraySeries materialize() {

            int h = includePositions.size();

            Object[] data = new Object[h];

            for (int i = 0; i < h; i++) {
                data[i] = get(i);
            }

            return new ArraySeries(data);
        }
    }
}
