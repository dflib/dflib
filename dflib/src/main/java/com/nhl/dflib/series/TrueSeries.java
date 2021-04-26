package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;

import java.util.Arrays;

/**
 * @since 0.11
 */
public class TrueSeries extends BooleanBaseSeries {

    private final int size;

    public TrueSeries(int size) {
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean getBoolean(int index) {
        return true;
    }

    @Override
    public void copyToBoolean(boolean[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, true);
    }

    @Override
    public BooleanSeries materializeBoolean() {
        return this;
    }

    @Override
    public BooleanSeries rangeOpenClosedBoolean(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new TrueSeries(toExclusive - fromInclusive);
    }

    @Override
    public BooleanSeries headBoolean(int len) {
        return len < size ? new TrueSeries(len) : this;
    }

    @Override
    public BooleanSeries tailBoolean(int len) {
        return len < size ? new TrueSeries(len) : this;
    }

    @Override
    public int countTrue() {
        return 0;
    }

    @Override
    public int countFalse() {
        return size;
    }
}
