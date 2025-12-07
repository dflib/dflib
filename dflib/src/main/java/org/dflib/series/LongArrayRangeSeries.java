package org.dflib.series;

import org.dflib.LongSeries;
import org.dflib.agg.Average;
import org.dflib.agg.CumSum;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.agg.Sum;
import org.dflib.range.Range;

/**
 * A specialized LongSeries that maps to a slice of an array. Calculating offsets during every operation has some
 * performance overhead, so this Series is somewhat slower than {@link LongArraySeries}.
 */
public class LongArrayRangeSeries extends LongBaseSeries {

    private final long[] data;
    private final int offset;
    private final int size;

    public LongArrayRangeSeries(long[] data, int offset, int size) {

        Range.checkRange(offset, size, data.length);

        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public LongSeries add(LongSeries s) {
        if (!(s instanceof LongArrayRangeSeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        long[] data = new long[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] + r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] + r[i];
            }
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries sub(LongSeries s) {
        if (!(s instanceof LongArrayRangeSeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        long[] data = new long[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] - r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] - r[i];
            }
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries mul(LongSeries s) {
        if (!(s instanceof LongArrayRangeSeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        long[] data = new long[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] * r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] * r[i];
            }
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries div(LongSeries s) {
        if (!(s instanceof LongArrayRangeSeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        long[] data = new long[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] / r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] / r[i];
            }
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries mod(LongSeries s) {
        if (!(s instanceof LongArrayRangeSeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        long[] data = new long[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] % r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] % r[i];
            }
        }

        return new LongArraySeries(data);
    }

    @Override
    public long getLong(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[offset + index];
    }

    @Override
    public void copyToLong(long[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
    }

    @Override
    public LongSeries rangeLong(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new LongArrayRangeSeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public LongSeries materialize() {
        if (offset == 0 && size == data.length) {
            return new LongArraySeries(data);
        }

        long[] data = new long[size];
        copyToLong(data, 0, 0, size);
        return new LongArraySeries(data);
    }

    @Override
    public long max() {
        return Max.ofArray(data, offset, size);
    }

    @Override
    public long min() {
        return Min.ofArray(data, offset, size);
    }

    @Override
    public long sum() {
        return Sum.ofArray(data, offset, size);
    }

    @Override
    public double avg() {
        return Average.ofArray(data, offset, size);
    }

    @Override
    public double quantile(double q) {
        return Percentiles.ofArray(data, offset, size, q);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = CumSum.ofArray(data, offset, size);
        return new LongArraySeries(cumSum);
    }
}
