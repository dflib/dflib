package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;

import java.util.Objects;

/**
 * A lazily-resolved LongSeries that is a subset of another IntSeries based on an IntSeries index. Most
 * operations are implemented as read-through on the underlying Series and do not cause "materialization".
 */
public class LongIndexedSeries extends LongBaseSeries {

    protected volatile Raw raw;
    protected volatile LongSeries materialized;

    public static Series<Long> of(LongSeries source, IntSeries includePositions) {

        // check for negative indices (that are found in joins) and return either an IntSeries or a Series<Integer>.
        int len = includePositions.size();
        for (int i = 0; i < len; i++) {
            if (includePositions.getInt(i) < 0) {
                return new IndexedSeries<>(source, includePositions);
            }
        }

        return new LongIndexedSeries(source, includePositions);
    }

    public LongIndexedSeries(LongSeries source, IntSeries includePositions) {
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
    public long getLong(int index) {
        Raw raw = this.raw;
        return raw != null ? raw.getLong(index) : materialized.getLong(index);
    }

    @Override
    public void copyToLong(long[] to, int fromOffset, int toOffset, int len) {
        materialize().copyToLong(to, fromOffset, toOffset, len);
    }

    @Override
    public LongSeries rangeLong(int fromInclusive, int toExclusive) {
        Raw raw = this.raw;
        return raw != null
                ? new LongIndexedSeries(raw.source, raw.includePositions.rangeInt(fromInclusive, toExclusive))
                : materialized.rangeLong(fromInclusive, toExclusive);
    }

    @Override
    public LongSeries cumSum() {
        return materialize().cumSum();
    }

    @Override
    public long max() {
        return materialize().max();
    }

    @Override
    public long min() {
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
    public double quantile(double q) {
        return materialize().quantile(q);
    }

    @Override
    public LongSeries materialize() {
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
        final LongSeries source;
        final IntSeries includePositions;

        Raw(LongSeries source, IntSeries includePositions) {
            this.source = Objects.requireNonNull(source);
            this.includePositions = Objects.requireNonNull(includePositions);
        }

        int size() {
            return includePositions.size();
        }

        long getLong(int index) {
            return source.getLong(includePositions.getInt(index));
        }

        LongSeries materialize() {

            int h = includePositions.size();

            long[] data = new long[h];

            for (int i = 0; i < h; i++) {
                data[i] = getLong(i);
            }

            return new LongArraySeries(data);
        }
    }
}
