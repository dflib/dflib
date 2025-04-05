package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.agg.PrimitiveSeriesAvg;
import org.dflib.agg.PrimitiveSeriesSum;
import org.dflib.range.Range;

/**
 * A specialized IntSeries that maps to a slice of an array. Calculating offsets during every operation has some
 * performance overhead, so this Series is somewhat slower than {@link IntArraySeries}.
 */
public class IntArrayRangeSeries extends IntBaseSeries {

    private final int[] data;
    private final int offset;
    private final int size;

    public IntArrayRangeSeries(int[] data, int offset, int size) {

        Range.checkRange(offset, size, data.length);

        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    // oddly enough, the same optimization of the "eq" method with IntArraySeries cast only shows about 3% speed
    // improvement, so we are ignoring it. While reimplementing "add" gives 33% improvement vs super.

    @Override
    public IntSeries add(IntSeries s) {

        if (!(s instanceof IntArrayRangeSeries)) {
            return super.add(s);
        }

        // TODO: special handling for IntArraySeries?

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArrayRangeSeries as = (IntArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        int[] data = new int[len];

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

        return new IntArraySeries(data);
    }

    @Override
    public IntSeries sub(IntSeries s) {
        if (!(s instanceof IntArrayRangeSeries)) {
            return super.sub(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArrayRangeSeries as = (IntArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        int[] data = new int[len];

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

        return new IntArraySeries(data);
    }

    @Override
    public IntSeries mul(IntSeries s) {
        if (!(s instanceof IntArrayRangeSeries)) {
            return super.mul(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArrayRangeSeries as = (IntArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        int[] data = new int[len];

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

        return new IntArraySeries(data);
    }

    @Override
    public IntSeries div(IntSeries s) {
        if (!(s instanceof IntArrayRangeSeries)) {
            return super.div(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArrayRangeSeries as = (IntArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        int[] data = new int[len];

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

        return new IntArraySeries(data);
    }

    @Override
    public IntSeries mod(IntSeries s) {
        if (!(s instanceof IntArrayRangeSeries)) {
            return super.mod(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArrayRangeSeries as = (IntArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        int[] data = new int[len];

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

        return new IntArraySeries(data);
    }

    @Override
    public int getInt(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[offset + index];
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
    }

    @Override
    public IntSeries rangeInt(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new IntArrayRangeSeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public IntSeries materialize() {
        if (offset == 0 && size == data.length) {
            return new IntArraySeries(data);
        }

        int[] data = new int[size];
        copyToInt(data, 0, 0, size);
        return new IntArraySeries(data);
    }

    @Override
    public int max() {
        return Max.ofArray(data, offset, size);
    }

    @Override
    public int min() {
        return Min.ofArray(data, offset, size);
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
    public double quantile(double q) {
        return Percentiles.ofArray(data, offset, size, q);
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfArray(data, offset, size);
        return new LongArraySeries(cumSum);
    }
}
