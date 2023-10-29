package com.nhl.dflib.series;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.agg.PrimitiveSeriesSum;
import com.nhl.dflib.range.Range;

import java.util.Arrays;

/**
 * @since 0.19
 */
public class LongSingleValueSeries extends LongBaseSeries {

    private final long value;
    private final Long boxedValue;
    private final int size;

    public LongSingleValueSeries(long value, int size) {
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
    public Long get(int index) {
        return boxedValue;
    }

    @Override
    public long getLong(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return value;
    }

    @Override
    public void copyToLong(long[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, value);
    }

    @Override
    public LongSeries materialize() {
        return this;
    }

    @Override
    public LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive) {

        if (fromInclusive == toExclusive) {
            return new LongArraySeries();
        }

        if (fromInclusive == 0 && toExclusive == size) {
            return this;
        }

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, size);
        return new LongSingleValueSeries(this.value, toExclusive - fromInclusive);
    }

    @Override
    public LongSeries head(int len) {

        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? tail(size + len) : new LongSingleValueSeries(value, len);
    }

    @Override
    public LongSeries tail(int len) {
        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? head(size + len) : new LongSingleValueSeries(value, len);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfValue(value, size);
        return new LongArraySeries(cumSum);
    }

    @Override
    public long max() {
        return value;
    }

    @Override
    public long min() {
        return value;
    }

    @Override
    public long sum() {
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
