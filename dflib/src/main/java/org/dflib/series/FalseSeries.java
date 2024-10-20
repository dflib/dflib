package org.dflib.series;

import org.dflib.BooleanSeries;

import java.util.Arrays;


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
    public BooleanSeries materialize() {
        return this;
    }

    @Override
    public BooleanSeries rangeBool(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new FalseSeries(toExclusive - fromInclusive);
    }

    @Override
    public boolean isFalse() {
        return size > 0;
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    @Override
    public int firstTrue() {
        return -1;
    }

    @Override
    public int firstFalse() {
        return size > 0 ? 0 : -1;
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
