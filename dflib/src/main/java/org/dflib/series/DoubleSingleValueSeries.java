package org.dflib.series;

import org.dflib.DoubleSeries;
import org.dflib.Series;
import org.dflib.agg.PrimitiveSeriesSum;
import org.dflib.range.Range;

import java.util.Arrays;

public class DoubleSingleValueSeries extends DoubleBaseSeries {

    private final double value;
    private final Double boxedValue;
    private final int size;

    public DoubleSingleValueSeries(double value, int size) {
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
    public Double get(int index) {
        return boxedValue;
    }

    @Override
    public double getDouble(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return value;
    }

    @Override
    public void copyToDouble(double[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, value);
    }

    @Override
    public DoubleSeries diff(Series<? extends Double> other) {
        if (size == 0 || other.size() == 0) {
            return this;
        }

        for (Double t : other) {
            if (t != null && t == value) {
                return Series.ofDouble();
            }
        }

        return this;
    }

    @Override
    public DoubleSeries intersect(Series<? extends Double> other) {
        if (size == 0) {
            return this;
        } else if (other.size() == 0) {
            return Series.ofDouble();
        }

        for (Double t : other) {
            if (t != null && t == value) {
                return this;
            }
        }

        return Series.ofDouble();
    }

    @Override
    public DoubleSeries materialize() {
        return this;
    }

    @Override
    public DoubleSeries rangeDouble(int fromInclusive, int toExclusive) {

        if (fromInclusive == 0 && toExclusive == size) {
            return this;
        }

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, size);

        if (fromInclusive == toExclusive) {
            return new DoubleArraySeries();
        }

        return new DoubleSingleValueSeries(this.value, toExclusive - fromInclusive);
    }

    @Override
    public DoubleSeries cumSum() {
        double[] cumSum = PrimitiveSeriesSum.cumSumOfValue(value, size);
        return new DoubleArraySeries(cumSum);
    }

    @Override
    public double max() {
        return value;
    }

    @Override
    public double min() {
        return value;
    }

    @Override
    public double sum() {
        return value * size;
    }

    @Override
    public double avg() {
        return value;
    }

    @Override
    public double median() {
        return value;
    }
}
