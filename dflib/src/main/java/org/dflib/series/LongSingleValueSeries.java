package org.dflib.series;

import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.agg.CumSum;
import org.dflib.agg.PrimitiveSeriesSum;
import org.dflib.range.Range;

import java.util.Arrays;

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
    public LongSeries diff(Series<? extends Long> other) {
        if (size == 0 || other.size() == 0) {
            return this;
        }

        for (Long t : other) {
            if (t != null && t == value) {
                return Series.ofLong();
            }
        }

        return this;
    }

    @Override
    public LongSeries intersect(Series<? extends Long> other) {
        if (size == 0) {
            return this;
        } else if (other.size() == 0) {
            return Series.ofLong();
        }

        for (Long t : other) {
            if (t != null && t == value) {
                return this;
            }
        }

        return Series.ofLong();
    }

    @Override
    public LongSeries materialize() {
        return this;
    }

    @Override
    public LongSeries rangeLong(int fromInclusive, int toExclusive) {

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
    public LongSeries cumSum() {
        long[] cumSum = CumSum.ofValue(value, size);
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

    @Override
    public double quantile(double q) {
        return value;
    }
}
