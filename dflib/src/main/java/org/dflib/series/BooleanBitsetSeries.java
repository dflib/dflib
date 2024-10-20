package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Sorter;
import org.dflib.builder.BitsetAccum;
import org.dflib.sort.SeriesSorter;

import java.util.Comparator;

/**
 * Implementation of {@link BooleanSeries} based on {@link FixedSizeBitSet}
 *
 * @since 2.0.0
 */
public class BooleanBitsetSeries extends BooleanBaseSeries {

    private final FixedSizeBitSet data;

    public BooleanBitsetSeries(FixedSizeBitSet data) {
        this.data = data;
    }

    @Override
    public boolean getBool(int index) {
        return data.get(index);
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = data.get(fromOffset + i);
        }
    }

    @Override
    public int size() {
        return data.getSize();
    }

    @Override
    public BooleanSeries materialize() {
        return this;
    }

    // TODO: could we override and optimize "concatBool" for a case others are Bitset-based too

    @Override
    public BooleanSeries rangeBool(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new BooleanBitsetSeries(data.range(fromInclusive, toExclusive));
    }

    @Override
    public BooleanSeries select(BooleanSeries positions) {
        int h = positions.size();
        BitsetAccum accum = new BitsetAccum(h);
        for (int i = 0; i < h; i++) {
            if (positions.getBool(i)) {
                if (this.data.get(i)) {
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
            if (this.data.get(index)) {
                accum.pushBool(true);
            }
        }
        return accum.toSeries();
    }

    @Override
    public int firstTrue() {
        return data.firstTrue();
    }

    @Override
    public int firstFalse() {
        return data.firstFalse();
    }

    @Override
    public BooleanSeries and(BooleanSeries another) {
        if (another instanceof BooleanBitsetSeries) {
            return new BooleanBitsetSeries(data.and(((BooleanBitsetSeries) another).data));
        }
        return super.and(another);
    }

    @Override
    public BooleanSeries or(BooleanSeries another) {
        if (another instanceof BooleanBitsetSeries) {
            return new BooleanBitsetSeries(data.or(((BooleanBitsetSeries) another).data));
        }
        return super.or(another);
    }

    @Override
    public BooleanBitsetSeries not() {
        return new BooleanBitsetSeries(data.not());
    }

    @Override
    public int countTrue() {
        return data.countTrue();
    }

    @Override
    public int countFalse() {
        return data.countFalse();
    }
}
