package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.agg.PrimitiveSeriesAvg;
import com.nhl.dflib.agg.PrimitiveSeriesMedian;
import com.nhl.dflib.agg.PrimitiveSeriesMinMax;
import com.nhl.dflib.agg.PrimitiveSeriesSum;

/**
 * An {@link com.nhl.dflib.IntSeries} that represents a range of sequential integers.
 *
 * @since 0.6
 */
public class IntSequenceSeries extends IntBaseSeries {

    private final int first;
    private final int lastExclusive;

    public IntSequenceSeries(int first, int lastExclusive) {
        this.first = first;
        this.lastExclusive = lastExclusive;
    }

    @Override
    public int getInt(int index) {
        int i = first + index;
        if (i >= lastExclusive) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return i;
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > lastExclusive) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        for (int i = 0; i < len; i++) {
            to[toOffset + i] = first + i;
        }
    }

    @Override
    public IntSeries materializeInt() {
        return this;
    }

    @Override
    public IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new IntSequenceSeries(first + fromInclusive, lastExclusive + toExclusive);
    }

    @Override
    public IntSeries headInt(int len) {
        return len < size() ? new IntSequenceSeries(first, first + len) : this;
    }

    @Override
    public IntSeries tailInt(int len) {
        return len < size() ? new IntSequenceSeries(first + size() - len, lastExclusive) : this;
    }

    @Override
    public int size() {
        return lastExclusive - first;
    }

    @Override
    public int max() {
        return PrimitiveSeriesMinMax.maxOfRange(lastExclusive);
    }

    @Override
    public int min() {
        return PrimitiveSeriesMinMax.minOfRange(first);
    }

    @Override
    public long sum() {
        return PrimitiveSeriesSum.sumOfRange(first, lastExclusive);
    }

    @Override
    public double avg() {
        return PrimitiveSeriesAvg.avgOfRange(first, lastExclusive);
    }

    @Override
    public double median() {
        return PrimitiveSeriesMedian.medianOfRange(first, lastExclusive);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfRange(first, lastExclusive);
        return new LongArraySeries(cumSum);
    }
}
