package com.nhl.dflib.series;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Series;

public class DoubleSingleValueSeries extends DoubleBaseSeries {

    private final double value;
    private final int size;

    public DoubleSingleValueSeries(double value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public double getDouble(int index) {
        return value;
    }

    @Override
    public void copyToDouble(double[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        int targetIdx = toOffset;
        for (int i = fromOffset; i < len; i++) {
            to[targetIdx++] = get(i);
        }
    }

    @Override
    public DoubleSeries materializeDouble() {
        return this;
    }

    @Override
    public DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new DoubleSingleValueSeries(value, toExclusive - fromInclusive);
    }

    @Override
    public DoubleSeries headDouble(int len) {
        return len < size ? new DoubleSingleValueSeries(value, len) : this;
    }

    @Override
    public DoubleSeries tailDouble(int len) {
        return len < size ? new DoubleSingleValueSeries(value, len) : this;
    }

    @Override
    public double max() {
        return value;
    }

    @Override
    public double min() {
        return value;
    }

    @Override
    public double sum() {
        return value * size;
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
