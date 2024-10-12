package org.dflib.series;

import org.dflib.DoubleSeries;
import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.Objects;

/**
 * A lazily-resolved DoubleSeries that is a subset of another DoubleSeries based on an IntSeries index. Most
 * operations are implemented as read-through on the underlying Series and do not cause "materialization".
 */
public class DoubleIndexedSeries extends DoubleBaseSeries {

    protected volatile Raw raw;
    protected volatile DoubleSeries materialized;

    public static Series<Double> of(DoubleSeries source, IntSeries includePositions) {

        // check for negative indices (that are found in joins) and return either an IntSeries or a Series<Integer>.
        int len = includePositions.size();
        for (int i = 0; i < len; i++) {
            if (includePositions.getInt(i) < 0) {
                return new IndexedSeries<>(source, includePositions);
            }
        }

        return new DoubleIndexedSeries(source, includePositions);
    }

    public DoubleIndexedSeries(DoubleSeries source, IntSeries includePositions) {
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
    public double getDouble(int index) {
        Raw raw = this.raw;
        return raw != null ? raw.getDouble(index) : materialized.getDouble(index);
    }

    @Override
    public void copyToDouble(double[] to, int fromOffset, int toOffset, int len) {
        materialize().copyToDouble(to, fromOffset, toOffset, len);
    }

    @Override
    public DoubleSeries rangeDouble(int fromInclusive, int toExclusive) {
        Raw raw = this.raw;
        return raw != null
                ? new DoubleIndexedSeries(raw.source, raw.includePositions.rangeInt(fromInclusive, toExclusive))
                : materialized.rangeDouble(fromInclusive, toExclusive);
    }

    @Override
    public DoubleSeries cumSum() {
        return materialize().cumSum();
    }

    @Override
    public double max() {
        return materialize().max();
    }

    @Override
    public double min() {
        return materialize().min();
    }

    @Override
    public double sum() {
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
    public DoubleSeries materialize() {
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
        final DoubleSeries source;
        final IntSeries includePositions;

        Raw(DoubleSeries source, IntSeries includePositions) {
            this.source = Objects.requireNonNull(source);
            this.includePositions = Objects.requireNonNull(includePositions);
        }

        int size() {
            return includePositions.size();
        }

        double getDouble(int index) {
            return source.getDouble(includePositions.getInt(index));
        }

        DoubleSeries materialize() {

            int h = includePositions.size();

            double[] data = new double[h];

            for (int i = 0; i < h; i++) {
                data[i] = getDouble(i);
            }

            return new DoubleArraySeries(data);
        }
    }
}
