package com.nhl.dflib.series;

import com.nhl.dflib.FloatSeries;

public class FloatSingleValueSeries extends FloatBaseSeries {

    private final float value;
    private final int size;

    public FloatSingleValueSeries(float value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public float getFloat(int index) {
        return value;
    }

    @Override
    public void copyToFloat(float[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        int targetIdx = toOffset;
        for (int i = fromOffset; i < len; i++) {
            to[targetIdx++] = get(i);
        }
    }

    @Override
    public FloatSeries materializeFloat() {
        return this;
    }

    @Override
    public FloatSeries rangeOpenClosedFloat(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new FloatSingleValueSeries(value, toExclusive - fromInclusive);
    }

    @Override
    public FloatSeries headFloat(int len) {
        return len < size ? new FloatSingleValueSeries(value, len) : this;
    }

    @Override
    public FloatSeries tailFloat(int len) {
        return len < size ? new FloatSingleValueSeries(value, len) : this;
    }

    @Override
    public float max() {
        return value;
    }

    @Override
    public float min() {
        return value;
    }

    @Override
    public float sum() {
        return value * size;
    }


    @Override
    public float avg() {
        return value;
    }

    @Override
    public float median() {
        return value;
    }
}
