package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.agg.PrimitiveSeriesCount;

/**
 * @since 0.6
 */
public class BooleanArraySeries extends BooleanBaseSeries {

    // TODO should we use a bitset instead of boolean[] for memory and access efficiency?
    private final boolean[] data;
    private final int offset;
    private final int size;

    public BooleanArraySeries(boolean... data) {
        this(data, 0, data.length);
    }

    public BooleanArraySeries(boolean[] data, int offset, int size) {
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
    public BooleanSeries headBool(int len) {
        return len < size ? new BooleanArraySeries(data, offset, len) : this;
    }

    @Override
    public BooleanSeries tailBool(int len) {
        return len < size ? new BooleanArraySeries(data, offset + size - len, len) : this;
    }

    @Override
    public BooleanSeries rangeOpenClosedBool(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new BooleanArraySeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public BooleanSeries materializeBool() {
        if (offset > 0 || size + offset < this.data.length) {
            boolean[] data = new boolean[size];
            copyToBool(data, 0, 0, size);
            return new BooleanArraySeries(data);
        }

        return this;
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
