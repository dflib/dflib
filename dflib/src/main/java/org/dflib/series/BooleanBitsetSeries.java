package org.dflib.series;

import org.dflib.BooleanSeries;

import java.util.BitSet;

public class BooleanBitsetSeries extends BooleanBaseSeries {

    private final BitSet data;

    public BooleanBitsetSeries(BitSet data) {
        this.data = data;
    }

    @Override
    public boolean getBool(int index) {
        return data.get(index);
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public BooleanSeries materialize() {
        return this;
    }

    @Override
    public BooleanSeries rangeBool(int fromInclusive, int toExclusive) {
        return new BooleanBitsetSeries(data.get(fromInclusive, toExclusive));
    }

    @Override
    public int firstTrue() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int countTrue() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int countFalse() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int size() {
        return data.size();
    }
}
