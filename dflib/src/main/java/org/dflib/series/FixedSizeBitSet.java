package org.dflib.series;

import java.util.Objects;

/**
 * Fixed-sized bitset structure that stores data in a bit-packed format.
 * <br/>
 * The size of this set is defined at the creation time.
 * <br/>
 * Optimized for the internal usage in Series implementations,
 * so this class skips some checks (like a range check) and provides some dirtier API than a {@link java.util.BitSet}
 *
 * @since 1.1.0
 */
public class FixedSizeBitSet {

    /**
     * Shift bits that's equivalent of a divide by {@link Long#SIZE} op
     */
    private static final int INDEX_BIT_SHIFT = 6;
    private static final long LONG_BITS_MASK = 0xFFFFFFFF_FFFFFFFFL;

    public static final FixedSizeBitSet EMPTY = new FixedSizeBitSet(new long[0], 0);

    /**
     * Packed long array that holds actual meaningful bits
     */
    final long[] data; // package private for testing

    /**
     * Size in terms of boolean values this set holds.
     * <br>
     * We can't calculate this from the {@code data.length}, so this value stored explicitly.
     */
    private final int size;

    /**
     * Construct a new BitSet with a defined size and content.
     *
     * @param data bits packed to a long[]
     * @param size desired size of the bit set
     */
    public FixedSizeBitSet(long[] data, int size) {
        if (arraySize(size) < data.length) {
            throw new IllegalArgumentException("Size mismatch");
        }
        this.data = data;
        this.size = size;
    }

    /**
     * Get value of a given bit
     *
     * @param index of the bit
     * @return value of the bit
     */
    public boolean get(int index) {
        int i = index >> INDEX_BIT_SHIFT;
        if(i >= data.length) {
            return false;
        }
        return (this.data[i] & (1L << index)) != 0;
    }

    public int countTrue() {
        int count = 0;
        for (long next : data) {
            if (next != 0) {
                count += Long.bitCount(next);
            }
        }
        return count;
    }

    public int countFalse() {
        return size - countTrue();
    }

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

    public int getSize() {
        return size;
    }

    public FixedSizeBitSet range(int from, int to) {
        if (size <= from || from == to) {
            return EMPTY;
        }

        if (to > size) {
            to = size;
        }

        int resultSize = arraySize(to - from);
        int setSize = to - from;
        long[] result = new long[resultSize];
        int startIndex = from >> INDEX_BIT_SHIFT;
        // is bits boundaries are shifted in the result
        boolean indexAligned = ((from & ((1 << INDEX_BIT_SHIFT) - 1)) == 0);

        // copy all elements except the last
        for (int i = 0; i < resultSize - 1; i++, startIndex++) {
            result[i] = indexAligned
                    ? data[startIndex]
                    : (data[startIndex] >>> from) | (data[startIndex + 1] << -from);
        }

        // copy the last element
        long lastElementMask = LONG_BITS_MASK >>> -to;
        result[resultSize - 1] =
                ((to - 1) & ((1 << INDEX_BIT_SHIFT) - 1)) < (from & ((1 << INDEX_BIT_SHIFT) - 1))
                        ? ((data[startIndex] >>> from) | (data[startIndex + 1] & lastElementMask) << -from)
                        : ((data[startIndex] & lastElementMask) >>> from);

        return new FixedSizeBitSet(result, setSize);
    }

    public FixedSizeBitSet not() {
        long[] newData = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            newData[i] = ~data[i];
        }
        return new FixedSizeBitSet(newData, getSize());
    }

    public FixedSizeBitSet or(FixedSizeBitSet bitSet) {
        Objects.requireNonNull(bitSet);
        if (bitSet.getSize() != getSize()) {
            throw new IllegalArgumentException("Argument differ in size");
        }
        long[] newData = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i] | bitSet.data[i];
        }
        return new FixedSizeBitSet(newData, getSize());
    }

    public FixedSizeBitSet and(FixedSizeBitSet bitSet) {
        Objects.requireNonNull(bitSet);
        if (bitSet.getSize() != getSize()) {
            throw new IllegalArgumentException("Argument differ in size");
        }
        long[] newData = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i] & bitSet.data[i];
        }
        return new FixedSizeBitSet(newData, getSize());
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
