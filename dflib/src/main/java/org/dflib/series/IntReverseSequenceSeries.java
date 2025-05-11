package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.agg.Percentiles;
import org.dflib.agg.PrimitiveSeriesAvg;
import org.dflib.agg.PrimitiveSeriesSum;
import org.dflib.range.Range;

/**
 * An {@link IntSeries} that represents a range of sequential integers going in decreasing order.
 *
 * @since 2.0.0
 */
public class IntReverseSequenceSeries extends IntBaseSeries {

    private final int seqFromInclusive;
    private final int seqToExclusive;

    public IntReverseSequenceSeries(int seqFromInclusive, int seqToExclusive) {

        if (seqFromInclusive < seqToExclusive) {
            throw new IllegalArgumentException("'first' sequence element is smaller than 'last' in a decreasing sequence: " + seqFromInclusive + " vs. " + seqToExclusive);
        }

        this.seqFromInclusive = seqFromInclusive;
        this.seqToExclusive = seqToExclusive;
    }

    @Override
    public int getInt(int index) {
        int i = seqFromInclusive - index;
        if (i <= seqToExclusive) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return i;
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        for (int i = 0; i < len; i++) {
            to[toOffset + i] = seqFromInclusive - fromOffset - i;
        }
    }

    @Override
    public IntSeries diff(Series<? extends Integer> other) {

        if (!(other instanceof IntReverseSequenceSeries)) {
            return super.diff(other);
        }

        int intersectFrom = Math.min(seqFromInclusive, ((IntReverseSequenceSeries) other).seqFromInclusive);
        int intersectTo = Math.max(seqToExclusive, ((IntReverseSequenceSeries) other).seqToExclusive);

        if (intersectFrom <= intersectTo) {
            return Series.ofInt();
        }

        int head = seqFromInclusive - intersectFrom;
        int tail =  intersectTo - seqToExclusive;
        int len = head + tail;
        int[] data = new int[len];

        for (int i = 0; i < head; i++) {
            data[i] = seqFromInclusive - i;
        }

        for (int i = 0; i < tail; i++) {
            data[i + head] = intersectTo - i;
        }

        return Series.ofInt(data);
    }

    @Override
    public IntSeries intersect(Series<? extends Integer> other) {
        if (!(other instanceof IntReverseSequenceSeries)) {
            return super.intersect(other);
        }

        int from = Math.min(seqFromInclusive, ((IntReverseSequenceSeries) other).seqFromInclusive);
        int to = Math.max(seqToExclusive,  ((IntReverseSequenceSeries) other).seqToExclusive);

        return from > to ? new IntReverseSequenceSeries(from, to) : Series.ofInt();
    }

    @Override
    public IntSeries materialize() {
        return this;
    }

    @Override
    public IntSeries rangeInt(int fromInclusive, int toExclusive) {
        int len = size();
        if (fromInclusive == 0 && toExclusive == len) {
            return this;
        }

        int newLen = toExclusive - fromInclusive;
        int newFrom = this.seqFromInclusive - fromInclusive;
        int newTo = this.seqFromInclusive - toExclusive;

        Range.checkRange(fromInclusive, newLen, len);
        return new IntReverseSequenceSeries(newFrom, newTo);
    }

    @Override
    public int size() {
        return seqFromInclusive - seqToExclusive;
    }

    @Override
    public int max() {
        return seqFromInclusive;
    }

    @Override
    public int min() {
        return seqToExclusive - 1;
    }

    @Override
    public long sum() {
        return PrimitiveSeriesSum.sumOfRange(seqToExclusive + 1, seqFromInclusive + 1);
    }

    @Override
    public double avg() {
        return PrimitiveSeriesAvg.avgOfRange(seqToExclusive + 1, seqFromInclusive + 1);
    }

    @Override
    public double quantile(double q) {
        return Percentiles.ofRange(seqToExclusive + 1, seqFromInclusive + 1, q);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfRange(seqToExclusive + 1, seqFromInclusive + 1);
        return new LongArraySeries(cumSum);
    }
}
