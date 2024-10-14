package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Sorter;
import org.dflib.sort.SeriesSorter;

import java.util.Comparator;

/**
 * Implementation of the {@link BooleanSeries} based on the {@link FixedSizeBitSet}
 *
 * @since 1.0.0-RC1 // TODO: correct version
 */
public class BooleanBitSetSeries extends BooleanBaseSeries {

    FixedSizeBitSet bitSet;

    public BooleanBitSetSeries(FixedSizeBitSet bitSet) {
        this.bitSet = bitSet;
    }

    @Override
    public boolean getBool(int index) {
        return bitSet.get(index);
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        for(int i=0; i<len; i++) {
            to[toOffset + i] = bitSet.get(fromOffset + i);
        }
    }

    @Override
    public int size() {
        return bitSet.getSize();
    }

    @Override
    public Boolean get(int index) {
        return bitSet.get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        for(int i=0; i<len; i++) {
            to[toOffset + i] = bitSet.get(fromOffset + i);
        }
    }

    @Override
    public BooleanSeries materialize() {
        return this;
    }

    @Override
    public BooleanSeries concatBool(BooleanSeries... other) {
        return super.concatBool(other); // TODO: reimplement this
    }

    @Override
    public BooleanSeries rangeBool(int fromInclusive, int toExclusive) {
        return null; // TODO: implement this
    }

    @Override
    public BooleanSeries select(BooleanSeries positions) {
        // TODO: reimplement this, it will require a new implementation of BoolAccum
        return super.select(positions);
    }

    @Override
    public BooleanSeries sort(Sorter... sorters) {
        return selectAsBooleanSeries(new SeriesSorter<>(this).sortIndex(sorters));
    }

    @Override
    public BooleanSeries sort(Comparator<? super Boolean> comparator) {
        return selectAsBooleanSeries(new SeriesSorter<>(this).sortIndex(comparator));
    }

    private BooleanSeries selectAsBooleanSeries(IntSeries positions) {
        int h = positions.size();
        FixedSizeBitSet bitSet = new FixedSizeBitSet(h);
        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            if(this.bitSet.get(index)) {
                bitSet.set(index);
            }
        }
        return new BooleanBitSetSeries(bitSet);
    }

    @Override
    public int firstTrue() {
        return bitSet.firstTrue();
    }

    @Override
    public boolean isTrue() {
        int firstTrue = bitSet.firstTrue();
        return firstTrue != -1;
    }

    @Override
    public boolean isFalse() {
        int firstTrue = bitSet.firstTrue();
        return firstTrue == -1;
    }

    @Override
    public BooleanSeries and(BooleanSeries another) {
        // TODO: could optimize this in case another is instance of BooleanBitSetSeries too
        return super.and(another);
    }

    @Override
    public BooleanSeries or(BooleanSeries another) {
        // TODO: could optimize this in case another is instance of BooleanBitSetSeries too
        return super.or(another);
    }

    @Override
    public BooleanSeries not() {
        return new BooleanBitSetSeries(bitSet.not());
    }

    @Override
    public int countTrue() {
        return bitSet.countTrue();
    }

    @Override
    public int countFalse() {
        return bitSet.countFalse();
    }
}
