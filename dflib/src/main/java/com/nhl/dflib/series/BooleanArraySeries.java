package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;

/**
 * @since 0.6
 */
public class BooleanArraySeries extends BooleanBaseSeries {

    // TODO should we use a bitset instead of boolean[] for memory and access efficiency?
    private boolean[] data;
    private int offset;
    private int size;

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
    public boolean getBoolean(int index) {
        if (offset + index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[offset + index];
    }

    @Override
    public void copyToBoolean(boolean[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
    }

    @Override
    public BooleanSeries headBoolean(int len) {
        return len < size ? new BooleanArraySeries(data, offset, len) : this;
    }

    @Override
    public BooleanSeries tailBoolean(int len) {
        return len < size ? new BooleanArraySeries(data, offset + size - len, len) : this;
    }

    @Override
    public BooleanSeries rangeOpenClosedBoolean(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new BooleanArraySeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public BooleanSeries materializeBoolean() {
        if (offset > 0 || size + offset < this.data.length) {
            boolean[] data = new boolean[size];
            copyToBoolean(data, 0, 0, size);
            return new BooleanArraySeries(data);
        }

        return this;
    }
}
