package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.Objects;

/**
 * A lazily-resolved Series that is a subset of another Series based on an IntSeries index. Most operations are implemented
 * as read-through on the underlying Series and do not cause "materialization".
 *
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
        Raw<T> raw = this.raw;
        return raw != null ? raw.size() : materialized.size();
    }

    @Override
    public T get(int index) {
        Raw<T> raw = this.raw;
        return raw != null ? raw.get(index) : materialized.get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        materialize().copyTo(to, fromOffset, toOffset, len);
    }

    @Override
    public Series<T> rangeOpenClosed(int fromInclusive, int toExclusive) {
        Raw<T> raw = this.raw;
        return raw != null
                ? new IndexedSeries(raw.source, raw.includePositions.rangeOpenClosedInt(fromInclusive, toExclusive))
                : materialized.rangeOpenClosed(fromInclusive, toExclusive);
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

    protected static class Raw<T> {
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

        ArraySeries<T> materialize() {

            int h = includePositions.size();

            Object[] data = new Object[h];

            for (int i = 0; i < h; i++) {
                data[i] = get(i);
            }

            return new ArraySeries(data);
        }
    }
}
