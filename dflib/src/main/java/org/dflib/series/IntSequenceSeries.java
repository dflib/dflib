package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.agg.Percentiles;
import org.dflib.agg.PrimitiveSeriesAvg;
import org.dflib.agg.PrimitiveSeriesSum;
import org.dflib.range.Range;

/**
 * An {@link IntSeries} that represents a range of sequential integers.
 */
public class IntSequenceSeries extends IntBaseSeries {

    private final int seqFromInclusive;
    private final int seqToExclusive;

    public IntSequenceSeries(int seqFromInclusive, int seqToExclusive) {

        if (seqFromInclusive > seqToExclusive) {
            throw new IllegalArgumentException("'first' sequence element is larger than 'last': " + seqFromInclusive + " vs. " + seqToExclusive);
        }

        this.seqFromInclusive = seqFromInclusive;
        this.seqToExclusive = seqToExclusive;
    }

    @Override
    public int getInt(int index) {
        int i = seqFromInclusive + index;
        if (i >= seqToExclusive) {
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
            to[toOffset + i] = fromOffset + seqFromInclusive + i;
        }
    }

    @Override
    public IntSeries diff(Series<? extends Integer> other) {

        if (!(other instanceof IntSequenceSeries)) {
            return super.diff(other);
        }

        int intersectFrom = Math.max(seqFromInclusive, ((IntSequenceSeries) other).seqFromInclusive);
        int intersectTo = Math.min(seqToExclusive, ((IntSequenceSeries) other).seqToExclusive);

        if (intersectFrom >= intersectTo) {
            return Series.ofInt();
        }

        int head = intersectFrom - seqFromInclusive;
        int tail = seqToExclusive - intersectTo;
        int len = head + tail;
        int[] data = new int[len];

        for (int i = 0; i < head; i++) {
            data[i] = seqFromInclusive + i;
        }

        for (int i = 0; i < tail; i++) {
            data[i + head] = intersectTo + i;
        }

        return Series.ofInt(data);
    }

    @Override
    public IntSeries intersect(Series<? extends Integer> other) {
        if (!(other instanceof IntSequenceSeries)) {
            return super.intersect(other);
        }

        int from = Math.max(seqFromInclusive, ((IntSequenceSeries) other).seqFromInclusive);
        int to = Math.min(seqToExclusive, ((IntSequenceSeries) other).seqToExclusive);

        return from < to ? new IntSequenceSeries(from, to) : Series.ofInt();
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
        int newFrom = this.seqFromInclusive + fromInclusive;
        int newTo = this.seqFromInclusive + toExclusive;

        Range.checkRange(fromInclusive, newLen, len);
        return new IntSequenceSeries(newFrom, newTo);
    }

    @Override
    public int size() {
        return seqToExclusive - seqFromInclusive;
    }

    @Override
    public int max() {
        return seqToExclusive - 1;
    }

    @Override
    public int min() {
        return seqFromInclusive;
    }

    @Override
    public long sum() {
        return PrimitiveSeriesSum.sumOfRange(seqFromInclusive, seqToExclusive);
    }

    @Override
    public double avg() {
        return PrimitiveSeriesAvg.avgOfRange(seqFromInclusive, seqToExclusive);
    }

    @Override
    public double quantile(double q) {
        return Percentiles.ofRange(seqFromInclusive, seqToExclusive, q);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfRange(seqFromInclusive, seqToExclusive);
        return new LongArraySeries(cumSum);
    }
}
