package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;

public class IntSingleValueSeries extends IntBaseSeries {

    private final int value;
    private final int size;

    public IntSingleValueSeries(int value, int size) {
        this.value = value;
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
        return value;
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        int targetIdx = toOffset;
        for (int i = fromOffset; i < len; i++) {
            to[targetIdx++] = get(i);
        }
    }

    @Override
    public IntSeries materializeInt() {
        return this;
    }

    @Override
    public IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new IntSingleValueSeries(value, toExclusive - fromInclusive);
    }

    @Override
    public IntSeries headInt(int len) {
        return len < size ? new IntSingleValueSeries(value, len) : this;
    }

    @Override
    public IntSeries tailInt(int len) {
        return len < size ? new IntSingleValueSeries(value, len) : this;
    }

    @Override
    public int max() {
        return value;
    }

    @Override
    public int min() {
        return value;
    }

    @Override
    public long sum() {
        return (long) value * size;
    }

    @Override
    public double avg() {
        return value;
    }

    @Override
    public double median() {
        return value;
    }
}
