package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Sorter;
import org.dflib.builder.BoolAccum;
import org.dflib.sort.SeriesSorter;

import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Implementation of {@link BooleanSeries} based on a bit set logic over {@code long[]}
 *
 * @since 2.0.0
 */
public class BooleanBitsetSeries extends BooleanBaseSeries {

    /**
     * Shift bits that's equivalent of a divide by {@link Long#SIZE} op
     */
    private static final int INDEX_BIT_SHIFT = 6;
    private static final long LONG_BITS_MASK = 0xFFFFFFFF_FFFFFFFFL;

    // package private for testing
    final long[] data;
    private final int size;

    public BooleanBitsetSeries(long[] data, int size) {
        this.data = data;
        this.size = size;
    }

    @Override
    public boolean getBool(int index) {
        int i = index >> INDEX_BIT_SHIFT;
        if (i >= data.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + data.length);
        }
        return (this.data[i] & (1L << index)) != 0;
    }

    private boolean getUnchecked(int index) {
        return (this.data[index >> INDEX_BIT_SHIFT] & (1L << index)) != 0;
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getUnchecked(fromOffset + i);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public BooleanSeries materialize() {
        return this;
    }

    // TODO: could we override and optimize "concatBool" for a case others are Bitset-based too

    @Override
    public BooleanSeries rangeBool(int fromInclusive, int toExclusive) {
        if (fromInclusive == 0 && toExclusive == size()) {
            return this;
        }

        if (size <= fromInclusive || fromInclusive == toExclusive) {
            return new FalseSeries(0);
        }

        if (toExclusive > size) {
            toExclusive = size;
        }

        int resultSize = arraySize(toExclusive - fromInclusive);
        int setSize = toExclusive - fromInclusive;
        long[] result = new long[resultSize];
        int startIndex = fromInclusive >> INDEX_BIT_SHIFT;
        // is bits boundaries are shifted in the result
        boolean indexAligned = ((fromInclusive & ((1 << INDEX_BIT_SHIFT) - 1)) == 0);

        // copy all elements except the last
        for (int i = 0; i < resultSize - 1; i++, startIndex++) {
            result[i] = indexAligned
                    ? data[startIndex]
                    : (data[startIndex] >>> fromInclusive) | (data[startIndex + 1] << -fromInclusive);
        }

        // copy the last element
        long lastElementMask = LONG_BITS_MASK >>> -toExclusive;
        result[resultSize - 1] =
                ((toExclusive - 1) & ((1 << INDEX_BIT_SHIFT) - 1)) < (fromInclusive & ((1 << INDEX_BIT_SHIFT) - 1))
                        ? ((data[startIndex] >>> fromInclusive) | (data[startIndex + 1] & lastElementMask) << -fromInclusive)
                        : ((data[startIndex] & lastElementMask) >>> fromInclusive);

        return new BooleanBitsetSeries(result, setSize);
    }

    @Override
    public BooleanSeries select(Predicate<Boolean> p) {
        return selectAsBooleanSeries(index(p));
    }

    @Override
    public BooleanSeries select(BooleanSeries positions) {
        int len = positions.size();
        if (size != positions.size()) {
            throw new IllegalArgumentException("Positions size " + positions.size() + " is not the same as this size " + len);
        }

        // trim down final accum size, but keep it at maximum size, to minimize resize operations
        int firstTrue = firstTrue();
        BoolAccum accum = new BoolAccum(len - firstTrue);

        if (positions instanceof BooleanBitsetSeries) {
            BooleanBitsetSeries bitPositions = (BooleanBitsetSeries) positions;
            for (int i = 0; i < data.length; i++) {
                long pos = bitPositions.data[i];
                long val = data[i];
                for (int j = 0; j < Long.SIZE; j++) {
                    long mask = 1L << j;
                    if ((pos & mask) != 0) {
                        accum.pushBool((val & mask) != 0);
                    }
                }
            }
        } else {
            for (int i = firstTrue; i < len; i++) {
                if (positions.getBool(i)) {
                    accum.pushBool(getUnchecked(i));
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
        int len = positions.size();
        BoolAccum accum = new BoolAccum(len);
        for (int i = 0; i < len; i++) {
            int index = positions.getInt(i);
            accum.pushBool(getUnchecked(index));
        }
        return accum.toSeries();
    }

    @Override
    public int firstTrue() {
        for (int i = 0; i < data.length; i++) {
            long next = data[i];
            if (next != 0) {
                int index = Long.numberOfTrailingZeros(next);
                int realIndex = (i << INDEX_BIT_SHIFT) + index;
                return realIndex < size ? realIndex : -1;
            }
        }
        return -1;
    }

    @Override
    public int firstFalse() {
        for (int i = 0; i < data.length; i++) {
            long next = data[i];
            if (next == 0) {
                return i << INDEX_BIT_SHIFT;
            } else if (next != LONG_BITS_MASK) {
                int index = Long.numberOfTrailingZeros(~next);
                int realIndex = (i << INDEX_BIT_SHIFT) + index;
                return realIndex < size ? realIndex : -1;
            }
        }
        return -1;
    }

    @Override
    public BooleanSeries and(BooleanSeries another) {
        if (another instanceof BooleanBitsetSeries) {
            if (another.size() != size()) {
                throw new IllegalArgumentException("Argument differ in size");
            }
            long[] newData = new long[data.length];
            for (int i = 0; i < data.length; i++) {
                newData[i] = data[i] & ((BooleanBitsetSeries) another).data[i];
            }
            return new BooleanBitsetSeries(newData, size);
        }
        return super.and(another);
    }

    @Override
    public BooleanSeries or(BooleanSeries another) {
        if (another instanceof BooleanBitsetSeries) {
            if (another.size() != size()) {
                throw new IllegalArgumentException("Argument differ in size");
            }
            long[] newData = new long[data.length];
            for (int i = 0; i < data.length; i++) {
                newData[i] = data[i] | ((BooleanBitsetSeries) another).data[i];
            }
            return new BooleanBitsetSeries(newData, size);
        }
        return super.or(another);
    }

    @Override
    public BooleanBitsetSeries not() {
        long[] newData = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            newData[i] = ~data[i];
        }
        return new BooleanBitsetSeries(newData, size);
    }

    @Override
    public int countTrue() {
        int count = 0;
        for (long next : data) {
            if (next != 0) {
                count += Long.bitCount(next);
            }
        }
        return count;
    }

    @Override
    public int countFalse() {
        int count = size;
        for (long next : data) {
            if (next != 0) {
                count -= Long.bitCount(next);
            }
        }
        return count;
    }

    @Override
    public IntSeries cumSum() {

        int[] cumSum = new int[size];

        int s = 0;
        for (int i = 0; i < size; i++) {
            s += getBool(i) ? 1 : 0;
            cumSum[i] = s;
        }

        return new IntArraySeries(cumSum);
    }

    /**
     * Calculate the size of the set in longs, just a {@code ceil(size / Long.SIZE)}
     *
     * @param setSize desired size of the set in bits
     * @return number of the longs to use
     */
    private static int arraySize(int setSize) {
        return 1 + ((setSize - 1) >> INDEX_BIT_SHIFT);
    }
}
