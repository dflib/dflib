package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.agg.PrimitiveSeriesAvg;
import com.nhl.dflib.agg.PrimitiveSeriesMedian;
import com.nhl.dflib.agg.PrimitiveSeriesMinMax;
import com.nhl.dflib.agg.PrimitiveSeriesSum;
import com.nhl.dflib.range.Range;

/**
 * An {@link com.nhl.dflib.IntSeries} that represents a range of sequential integers.
 *
 * @since 0.6
 */
public class IntSequenceSeries extends IntBaseSeries {

    private final int fromInclusive;
    private final int toExclusive;

    public IntSequenceSeries(int fromInclusive, int toExclusive) {

        if (fromInclusive > toExclusive) {
            throw new IllegalArgumentException("'first' sequence element is larger than 'last': " + fromInclusive + " vs. " + toExclusive);
        }

        this.fromInclusive = fromInclusive;
        this.toExclusive = toExclusive;
    }

    @Override
    public int getInt(int index) {
        int i = fromInclusive + index;
        if (i >= toExclusive) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return i;
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > toExclusive) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        for (int i = 0; i < len; i++) {
            to[toOffset + i] = fromInclusive + i;
        }
    }

    @Override
    public IntSeries diff(Series<? extends Integer> other) {

        if (!(other instanceof IntSequenceSeries)) {
            return super.diff(other);
        }

        int otherFirst = ((IntSequenceSeries) other).fromInclusive;
        int otherLastExclusive = ((IntSequenceSeries) other).toExclusive;

        int head = otherFirst - fromInclusive;
        int tail = toExclusive - otherLastExclusive;

        if (head > 0) {
            if (tail <= 0) {
                return head(head);
            }

            int len = head + tail;
            int[] data = new int[len];
            for (int i = 0; i < head; i++) {
                data[i] = fromInclusive + i;
            }

            int tailOffset = toExclusive - tail - 1;
            for (int i = head; i < len; i++) {
                data[i] = tailOffset + i;
            }

            return Series.ofInt(data);

        } else if (tail > 0) {
            return tail(tail);
        } else {
            return Series.ofInt();
        }
    }

    @Override
    public IntSeries intersect(Series<? extends Integer> other) {
        if (!(other instanceof IntSequenceSeries)) {
            return super.intersect(other);
        }

        int otherFirst = ((IntSequenceSeries) other).fromInclusive;
        int otherLastExclusive = ((IntSequenceSeries) other).toExclusive;

        int head = otherFirst - fromInclusive;
        int tail = toExclusive - otherLastExclusive;

        if (head > 0) {
            return tail > 0 ? head(-head).tail(-tail) : head(-head);
        } else if (tail > 0) {
            return tail(-tail);
        } else {
            return this;
        }
    }

    @Override
    public IntSeries materialize() {
        return this;
    }

    @Override
    public IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive) {
        int len = size();
        if (fromInclusive == 0 && toExclusive == len) {
            return this;
        }

        int newLen = toExclusive - fromInclusive;
        int newFrom = this.fromInclusive + fromInclusive;
        int newTo = this.fromInclusive + toExclusive;

        Range.checkRange(fromInclusive, newLen, len);
        return new IntSequenceSeries(newFrom, newTo);
    }

    @Override
    public IntSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : rangeOpenClosedInt(0, len);
    }

    @Override
    public IntSeries tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : rangeOpenClosedInt(size - len, size);
    }

    @Override
    public int size() {
        return toExclusive - fromInclusive;
    }

    @Override
    public int max() {
        return PrimitiveSeriesMinMax.maxOfRange(toExclusive);
    }

    @Override
    public int min() {
        return PrimitiveSeriesMinMax.minOfRange(fromInclusive);
    }

    @Override
    public long sum() {
        return PrimitiveSeriesSum.sumOfRange(fromInclusive, toExclusive);
    }

    @Override
    public double avg() {
        return PrimitiveSeriesAvg.avgOfRange(fromInclusive, toExclusive);
    }

    @Override
    public double median() {
        return PrimitiveSeriesMedian.medianOfRange(fromInclusive, toExclusive);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfRange(fromInclusive, toExclusive);
        return new LongArraySeries(cumSum);
    }
}
