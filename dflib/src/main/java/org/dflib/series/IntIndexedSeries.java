package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;

import java.util.Objects;

/**
 * A lazily-resolved IntSeries that is a subset of another IntSeries based on an IntSeries index. Most
 * operations are implemented as read-through on the underlying Series and do not cause "materialization".
 *
 * @since 1.0.0-M19
 */
public class IntIndexedSeries extends IntBaseSeries {

    protected volatile Raw raw;
    protected volatile IntSeries materialized;

    public static Series<Integer> of(IntSeries source, IntSeries includePositions) {

        // check for negative indices (that are found in joins) and return either an IntSeries or a Series<Integer>.
        int len = includePositions.size();
        for (int i = 0; i < len; i++) {
            if (includePositions.getInt(i) < 0) {
                return new IndexedSeries<>(source, includePositions);
            }
        }

        return new IntIndexedSeries(source, includePositions);
    }

    public IntIndexedSeries(IntSeries source, IntSeries includePositions) {
        this.raw = new Raw(source, includePositions);
    }

    public boolean isMaterialized() {
        return materialized != null;
    }

    @Override
    public int size() {
        Raw raw = this.raw;
        return raw != null ? raw.size() : materialized.size();
    }

    @Override
    public int getInt(int index) {
        Raw raw = this.raw;
        return raw != null ? raw.getInt(index) : materialized.getInt(index);
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        materialize().copyToInt(to, fromOffset, toOffset, len);
    }

    @Override
    public IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive) {
        Raw raw = this.raw;
        return raw != null
                ? new IntIndexedSeries(raw.source, raw.includePositions.rangeOpenClosedInt(fromInclusive, toExclusive))
                : materialized.rangeOpenClosedInt(fromInclusive, toExclusive);
    }

    @Override
    public LongSeries cumSum() {
        return materialize().cumSum();
    }

    @Override
    public int max() {
        return materialize().max();
    }

    @Override
    public int min() {
        return materialize().min();
    }

    @Override
    public long sum() {
        return materialize().sum();
    }

    @Override
    public double avg() {
        return materialize().avg();
    }

    @Override
    public double median() {
        return materialize().median();
    }

    @Override
    public IntSeries materialize() {
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

    protected static class Raw {
        final IntSeries source;
        final IntSeries includePositions;

        Raw(IntSeries source, IntSeries includePositions) {
            this.source = Objects.requireNonNull(source);
            this.includePositions = Objects.requireNonNull(includePositions);
        }

        int size() {
            return includePositions.size();
        }

        int getInt(int index) {
            return source.getInt(includePositions.getInt(index));
        }

        IntSeries materialize() {

            int h = includePositions.size();

            int[] data = new int[h];

            for (int i = 0; i < h; i++) {
                data[i] = getInt(i);
            }

            return new IntArraySeries(data);
        }
    }
}
