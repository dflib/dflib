package com.nhl.dflib.series;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.aggregate.PrimitiveSeriesAvg;
import com.nhl.dflib.aggregate.PrimitiveSeriesMedian;
import com.nhl.dflib.aggregate.PrimitiveSeriesMinMax;
import com.nhl.dflib.aggregate.PrimitiveSeriesSum;

/**
 * @since 0.6
 */
public class LongArraySeries extends LongBaseSeries {

    private long[] data;
    private int offset;
    private int size;

    public LongArraySeries(long... data) {
        this(data, 0, data.length);
    }

    public LongArraySeries(long[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
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
    public LongSeries headLong(int len) {
        return len < size ? new LongArraySeries(data, offset, len) : this;
    }

    @Override
    public LongSeries tailLong(int len) {
        return len < size ? new LongArraySeries(data, offset + size - len, len) : this;
    }

    @Override
    public LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new LongArraySeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public LongSeries materializeLong() {
        if (offset > 0 || size + offset < this.data.length) {
            long[] data = new long[size];
            copyToLong(data, 0, 0, size);
            return new LongArraySeries(data);
        }

        return this;
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
}
