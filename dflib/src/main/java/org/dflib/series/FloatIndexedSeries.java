package org.dflib.series;

import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.Objects;

/**
 * A lazily-resolved FloatSeries that is a subset of another FloatSeries based on an IntSeries index. Most
 * operations are implemented as read-through on the underlying Series and do not cause "materialization".
 *
 * @since 1.1.0
 */
public class FloatIndexedSeries extends FloatBaseSeries {

    protected volatile Raw raw;
    protected volatile FloatSeries materialized;

    public static Series<Float> of(FloatSeries source, IntSeries includePositions) {

        // check for negative indices (that are found in joins) and return either an IntSeries or a Series<Integer>.
        int len = includePositions.size();
        for (int i = 0; i < len; i++) {
            if (includePositions.getInt(i) < 0) {
                return new IndexedSeries<>(source, includePositions);
            }
        }

        return new FloatIndexedSeries(source, includePositions);
    }

    public FloatIndexedSeries(FloatSeries source, IntSeries includePositions) {
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
    public float getFloat(int index) {
        Raw raw = this.raw;
        return raw != null ? raw.getFloat(index) : materialized.getFloat(index);
    }

    @Override
    public void copyToFloat(float[] to, int fromOffset, int toOffset, int len) {
        materialize().copyToFloat(to, fromOffset, toOffset, len);
    }

    @Override
    public FloatSeries rangeFloat(int fromInclusive, int toExclusive) {
        Raw raw = this.raw;
        return raw != null
                ? new FloatIndexedSeries(raw.source, raw.includePositions.rangeInt(fromInclusive, toExclusive))
                : materialized.rangeFloat(fromInclusive, toExclusive);
    }

    @Override
    public DoubleSeries cumSum() {
        return materialize().cumSum();
    }

    @Override
    public float max() {
        return materialize().max();
    }

    @Override
    public float min() {
        return materialize().min();
    }

    @Override
    public double sum() {
        return materialize().sum();
    }

    @Override
    public float avg() {
        return materialize().avg();
    }

    @Override
    public float quantile(double q) {
        return materialize().quantile(q);
    }

    @Override
    public FloatSeries materialize() {
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
        final FloatSeries source;
        final IntSeries includePositions;

        Raw(FloatSeries source, IntSeries includePositions) {
            this.source = Objects.requireNonNull(source);
            this.includePositions = Objects.requireNonNull(includePositions);
        }

        int size() {
            return includePositions.size();
        }

        float getFloat(int index) {
            return source.getFloat(includePositions.getInt(index));
        }

        FloatSeries materialize() {

            int h = includePositions.size();

            float[] data = new float[h];
            for (int i = 0; i < h; i++) {
                data[i] = getFloat(i);
            }

            return new FloatArraySeries(data);
        }
    }
}
