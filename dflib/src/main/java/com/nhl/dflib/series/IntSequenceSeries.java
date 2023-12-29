package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
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

        if (first > lastExclusive) {
            throw new IllegalArgumentException("'first' sequence element is larger than 'last': " + first + " vs. " + lastExclusive);
        }

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
    public IntSeries diff(Series<? extends Integer> other) {

        if (!(other instanceof IntSequenceSeries)) {
            return super.diff(other);
        }

        int otherFirst = ((IntSequenceSeries) other).first;
        int otherLastExclusive = ((IntSequenceSeries) other).lastExclusive;

        int head = otherFirst - first;
        int tail = lastExclusive - otherLastExclusive;

        if (head > 0) {
            if (tail <= 0) {
                return head(head);
            }

            int len = head + tail;
            int[] data = new int[len];
            for (int i = 0; i < head; i++) {
                data[i] = first + i;
            }

            int tailOffset = lastExclusive - tail - 1;
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

        int otherFirst = ((IntSequenceSeries) other).first;
        int otherLastExclusive = ((IntSequenceSeries) other).lastExclusive;

        int head = otherFirst - first;
        int tail = lastExclusive - otherLastExclusive;

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
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new IntSequenceSeries(first + fromInclusive, lastExclusive + toExclusive);
    }

    @Override
    public IntSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : new IntSequenceSeries(first, first + len);
    }

    @Override
    public IntSeries tail(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size() + len) : new IntSequenceSeries(first + size() - len, lastExclusive);
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
