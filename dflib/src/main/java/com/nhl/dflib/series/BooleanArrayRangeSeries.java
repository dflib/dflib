package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.agg.PrimitiveSeriesCount;

/**
 * A specialized DoubleSeries that maps to a slice of an array. Calculating offsets during every operation has some
 * performance overhead, so this Series is somewhat slower than {@link DoubleArraySeries}.
 *
 * @since 0.19
 */
public class BooleanArrayRangeSeries extends BooleanBaseSeries {

    private final boolean[] data;
    private final int offset;
    private final int size;

    public BooleanArrayRangeSeries(boolean[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean getBool(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[offset + index];
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
    }

    @Override
    public BooleanSeries head(int len) {
        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? tail(size + len) : new BooleanArrayRangeSeries(data, offset, len);
    }

    @Override
    public BooleanSeries tail(int len) {

        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? head(size + len) : new BooleanArrayRangeSeries(data, offset + size - len, len);
    }

    @Override
    public BooleanSeries rangeOpenClosedBool(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new BooleanArrayRangeSeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public BooleanSeries materialize() {
        if (offset == 0 && size == data.length) {
            return new BooleanArraySeries(data);
        }

        boolean[] data = new boolean[size];
        copyToBool(data, 0, 0, size);
        return new BooleanArraySeries(data);
    }

    @Override
    public int firstTrue() {
        for (int i = 0; i < size; i++) {
            if (data[offset + i]) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int countTrue() {
        return PrimitiveSeriesCount.countTrueInArray(data, offset, size);
    }

    @Override
    public int countFalse() {
        return PrimitiveSeriesCount.countFalseInArray(data, offset, size);
    }
}
