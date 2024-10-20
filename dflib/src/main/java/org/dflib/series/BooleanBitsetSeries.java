package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Sorter;
import org.dflib.builder.BitsetAccum;
import org.dflib.sort.SeriesSorter;

import java.util.Comparator;

/**
 * Implementation of the {@link BooleanSeries} based on the {@link FixedSizeBitSet}
 *
 * @since 1.1.0
 */
public class BooleanBitsetSeries extends BooleanBaseSeries {

    FixedSizeBitSet bitSet;

    public BooleanBitsetSeries(FixedSizeBitSet bitSet) {
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
        // TODO: could we optimize this for a case others are Bitset-based too
        return super.concatBool(other);
    }

    @Override
    public BooleanSeries rangeBool(int fromInclusive, int toExclusive) {
        return new BooleanBitsetSeries(bitSet.range(fromInclusive, toExclusive));
    }

    @Override
    public BooleanSeries select(BooleanSeries positions) {
        int h = positions.size();
        BitsetAccum accum = new BitsetAccum(h);
        for (int i = 0; i < h; i++) {
            if(positions.getBool(i)) {
                if (this.bitSet.get(i)) {
                    accum.pushBool(true);
                }
            }
        }
        return accum.toSeries();
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
        BitsetAccum accum = new BitsetAccum(h);
        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            if(this.bitSet.get(index)) {
                accum.pushBool(true);
            }
        }
        return accum.toSeries();
    }

    @Override
    public int firstTrue() {
        return bitSet.firstTrue();
    }

    @Override
    public boolean isTrue() {
        int firstFalse = bitSet.firstFalse();
        return firstFalse == -1;
    }

    @Override
    public boolean isFalse() {
        int firstTrue = bitSet.firstTrue();
        return firstTrue == -1;
    }

    @Override
    public BooleanSeries and(BooleanSeries another) {
        if(another instanceof BooleanBitsetSeries) {
            return new BooleanBitsetSeries(bitSet.and(((BooleanBitsetSeries) another).bitSet));
        }
        return super.and(another);
    }

    @Override
    public BooleanSeries or(BooleanSeries another) {
        if(another instanceof BooleanBitsetSeries) {
            return new BooleanBitsetSeries(bitSet.or(((BooleanBitsetSeries) another).bitSet));
        }
        return super.or(another);
    }

    @Override
    public BooleanBitsetSeries not() {
        return new BooleanBitsetSeries(bitSet.not());
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
