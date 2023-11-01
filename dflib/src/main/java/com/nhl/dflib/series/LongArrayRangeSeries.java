package com.nhl.dflib.series;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.agg.PrimitiveSeriesAvg;
import com.nhl.dflib.agg.PrimitiveSeriesMedian;
import com.nhl.dflib.agg.PrimitiveSeriesMinMax;
import com.nhl.dflib.agg.PrimitiveSeriesSum;
import com.nhl.dflib.range.Range;

/**
 * A specialized LongSeries that maps to a slice of an array. Calculating offsets during every operation has some
 * performance overhead, so this Series is somewhat slower than {@link LongArraySeries}.
 *
 * @since 0.19
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
        if (!(s instanceof LongArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArrayRangeSeries as = (LongArrayRangeSeries) s;

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
        if (!(s instanceof LongArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArrayRangeSeries as = (LongArrayRangeSeries) s;

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
        if (!(s instanceof LongArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArrayRangeSeries as = (LongArrayRangeSeries) s;

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
        if (!(s instanceof LongArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArrayRangeSeries as = (LongArrayRangeSeries) s;

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
        if (!(s instanceof LongArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArrayRangeSeries as = (LongArrayRangeSeries) s;

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
    public LongSeries head(int len) {

        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? tail(size + len) : new LongArrayRangeSeries(data, offset, len);
    }

    @Override
    public LongSeries tail(int len) {

        if (len < 0) {
            return head(size + len);
        }

        return len < size ? new LongArrayRangeSeries(data, offset + size - len, len) : this;
    }

    @Override
    public LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive) {
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
        return PrimitiveSeriesMinMax.maxOfArray(data, offset, size);
    }

    @Override
    public long min() {
        return PrimitiveSeriesMinMax.minOfArray(data, offset, size);
    }

    @Override
    public long sum() {
        return PrimitiveSeriesSum.sumOfArray(data, offset, size);
    }

    @Override
    public double avg() {
        return PrimitiveSeriesAvg.avgOfArray(data, offset, size);
    }

    @Override
    public double median() {
        return PrimitiveSeriesMedian.medianOfArray(data, offset, size);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfArray(data, offset, size);
        return new LongArraySeries(cumSum);
    }
}
