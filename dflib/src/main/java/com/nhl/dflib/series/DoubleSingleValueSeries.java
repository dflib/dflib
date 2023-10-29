package com.nhl.dflib.series;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.agg.PrimitiveSeriesSum;
import com.nhl.dflib.range.Range;

import java.util.Arrays;

/**
 * @since 0.19
 */
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
    public DoubleSeries materialize() {
        return this;
    }

    @Override
    public DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive) {

        if (fromInclusive == toExclusive) {
            return new DoubleArraySeries();
        }

        if (fromInclusive == 0 && toExclusive == size) {
            return this;
        }

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, size);
        return new DoubleSingleValueSeries(this.value, toExclusive - fromInclusive);
    }

    @Override
    public DoubleSeries head(int len) {

        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? tail(size + len) : new DoubleSingleValueSeries(value, len);
    }

    @Override
    public DoubleSeries tail(int len) {
        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? head(size + len) : new DoubleSingleValueSeries(value, len);
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
