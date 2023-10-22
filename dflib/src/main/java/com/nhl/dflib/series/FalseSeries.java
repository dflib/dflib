package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;

import java.util.Arrays;

/**
 * @since 0.11
 */
public class FalseSeries extends BooleanBaseSeries {

    private final int size;

    public FalseSeries(int size) {
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean getBool(int index) {
        return false;
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, false);
    }

    @Override
    public BooleanSeries materializeBool() {
        return this;
    }

    @Override
    public BooleanSeries rangeOpenClosedBool(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new FalseSeries(toExclusive - fromInclusive);
    }

    @Override
    public BooleanSeries head(int len) {
        return len < size ? new FalseSeries(len) : this;
    }

    @Override
    public BooleanSeries tail(int len) {
        return len < size ? new FalseSeries(len) : this;
    }

    @Override
    public int firstTrue() {
        return -1;
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
