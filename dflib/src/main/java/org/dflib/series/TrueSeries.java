package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;

import java.util.Arrays;


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
    public boolean getBool(int index) {
        return true;
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, true);
    }

    @Override
    public BooleanSeries materialize() {
        return this;
    }

    @Override
    public BooleanSeries rangeBool(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new TrueSeries(toExclusive - fromInclusive);
    }

    @Override
    public boolean isFalse() {
        return false;
    }

    @Override
    public boolean isTrue() {
        return size > 0;
    }

    @Override
    public int firstTrue() {
        return size > 0 ? 0 : -1;
    }

    @Override
    public int firstFalse() {
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

    @Override
    public IntSeries cumSum() {
        int[] cumSum = new int[size];
        for (int i = 0; i < size; i++) {
            cumSum[i] = i + 1;
        }
        return new IntArraySeries(cumSum);
    }
}
