package org.dflib.series;

import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.Series;
import org.dflib.agg.CumSum;
import org.dflib.agg.PrimitiveSeriesSum;
import org.dflib.range.Range;

import java.util.Arrays;

/**
 * @since 1.1.0
 */
public class FloatSingleValueSeries extends FloatBaseSeries {

    private final float value;
    private final Float boxedValue;
    private final int size;

    public FloatSingleValueSeries(float value, int size) {
        this.value = value;
        this.size = size;

        // caching boxed value for non-primitive operations
        this.boxedValue = value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Float get(int index) {
        return boxedValue;
    }

    @Override
    public float getFloat(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return value;
    }

    @Override
    public void copyToFloat(float[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, value);
    }

    @Override
    public FloatSeries diff(Series<? extends Float> other) {
        if (size == 0 || other.size() == 0) {
            return this;
        }

        for (Float t : other) {
            if (t != null && t == value) {
                return Series.ofFloat();
            }
        }

        return this;
    }

    @Override
    public FloatSeries intersect(Series<? extends Float> other) {
        if (size == 0) {
            return this;
        } else if (other.size() == 0) {
            return Series.ofFloat();
        }

        for (Float t : other) {
            if (t != null && t == value) {
                return this;
            }
        }

        return Series.ofFloat();
    }

    @Override
    public FloatSeries materialize() {
        return this;
    }

    @Override
    public FloatSeries rangeFloat(int fromInclusive, int toExclusive) {

        if (fromInclusive == 0 && toExclusive == size) {
            return this;
        }

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, size);

        if (fromInclusive == toExclusive) {
            return new FloatArraySeries();
        }

        return new FloatSingleValueSeries(this.value, toExclusive - fromInclusive);
    }

    @Override
    public DoubleSeries cumSum() {
        double[] cumSum = CumSum.ofValue(value, size);
        return new DoubleArraySeries(cumSum);
    }

    @Override
    public float max() {
        return value;
    }

    @Override
    public float min() {
        return value;
    }

    @Override
    public double sum() {
        return value * size;
    }

    @Override
    public float avg() {
        return value;
    }

    @Override
    public float median() {
        return value;
    }

    @Override
    public float quantile(double q) {
        return value;
    }
}
