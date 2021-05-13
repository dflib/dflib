package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.aggregate.PrimitiveSeriesAvg;
import com.nhl.dflib.aggregate.PrimitiveSeriesMedian;
import com.nhl.dflib.aggregate.PrimitiveSeriesMinMax;
import com.nhl.dflib.aggregate.PrimitiveSeriesSum;

/**
 * @since 0.6
 */
// TODO: should we split that into a fast IntArraySeries that matches exactly data[], and a slower IntArrayRangeSeries,
//  that has offset and size? Need to measure the performance gain of not having to calculate offset
public class IntArraySeries extends IntBaseSeries {

    private int[] data;
    private int offset;
    private int size;

    public IntArraySeries(int... data) {
        this(data, 0, data.length);
    }

    public IntArraySeries(int[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
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
    public IntSeries headInt(int len) {
        return len < size ? new IntArraySeries(data, offset, len) : this;
    }

    @Override
    public IntSeries tailInt(int len) {
        return len < size ? new IntArraySeries(data, offset + size - len, len) : this;
    }

    @Override
    public IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new IntArraySeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public IntSeries materializeInt() {
        if (offset > 0 || size + offset < this.data.length) {
            int[] data = new int[size];
            copyToInt(data, 0, 0, size);
            return new IntArraySeries(data);
        }

        return this;
    }

    @Override
    public int max() {
        return PrimitiveSeriesMinMax.maxOfArray(data, offset, size);
    }

    @Override
    public int min() {
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
