package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.agg.PrimitiveSeriesSum;
import com.nhl.dflib.range.Range;

import java.util.Arrays;

/**
 * @since 1.0.0-M19
 */
public class IntSingleValueSeries extends IntBaseSeries {

    private final int value;
    private final Integer boxedValue;
    private final int size;

    public IntSingleValueSeries(int value, int size) {
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
    public Integer get(int index) {
        return boxedValue;
    }

    @Override
    public int getInt(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return value;
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, value);
    }

    @Override
    public IntSeries diff(Series<? extends Integer> other) {
        if (size == 0 || other.size() == 0) {
            return this;
        }

        for (Integer t : other) {
            if (t != null && t == value) {
                return Series.ofInt();
            }
        }

        return this;
    }

    @Override
    public IntSeries intersect(Series<? extends Integer> other) {
        if (size == 0) {
            return this;
        } else if (other.size() == 0) {
            return Series.ofInt();
        }

        for (Integer t : other) {
            if (t != null && t == value) {
                return this;
            }
        }

        return Series.ofInt();
    }

    @Override
    public IntSeries materialize() {
        return this;
    }

    @Override
    public IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive) {

        if (fromInclusive == toExclusive) {
            return new IntArraySeries();
        }

        if (fromInclusive == 0 && toExclusive == size) {
            return this;
        }

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, size);
        return new IntSingleValueSeries(this.value, toExclusive - fromInclusive);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfValue(value, size);
        return new LongArraySeries(cumSum);
    }

    @Override
    public int max() {
        return value;
    }

    @Override
    public int min() {
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
