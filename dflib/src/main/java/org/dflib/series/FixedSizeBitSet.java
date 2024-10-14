package org.dflib.series;

/**
 * Fixed-sized bitset structure that stores data in a bit-packed format.
 * <br/>
 * The size of this set is defined at the creation time.
 * <br/>
 * Optimized for the internal usage in Series implementations,
 * so this class skips some checks (like a range check) and provides some dirtier API than a {@link java.util.BitSet}
 *
 * @since 1.0.0-RC1 // TODO: correct version
 */
public class FixedSizeBitSet {

    /**
     * Shift bits that's equivalent of a divide by {@link Long#SIZE} op
     */
    private static final int INDEX_BIT_SHIFT = 6;

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
     * Construct a new BitSet with a defined size.
     *
     * @param size desired size of the bit set
     */
    public FixedSizeBitSet(int size) {
        this.data = new long[arraySize(size)];
        this.size = size;
    }

    private FixedSizeBitSet(long[] data, int size) {
        this.data = data;
        this.size = size;
    }

    /**
     * Set n-th bit to the value
     * @param index of the bit to set
     * @param value of the bit to set
     */
    public void set(int index, boolean value) {
        if(value) {
            set(index);
        } else {
            clear(index);
        }
    }

    /**
     * Set a given bit to {@code false}
     * @param index of the bit
     */
    public void clear(int index) {
        data[index >> INDEX_BIT_SHIFT] &= ~(1L << index);
    }

    /**
     * Set a given bit to {@code true}
     * @param index of the bit
     */
    public void set(int index) {
        data[index >> INDEX_BIT_SHIFT] |= (1L << index);
    }

    /**
     * Get value of a given bit
     * @param index of the bit
     * @return value of the bit
     */
    public boolean get(int index) {
        return (this.data[index >> INDEX_BIT_SHIFT] & (1L << index)) != 0;
    }

    public int countTrue() {
        int count = 0;
        for(long next: data) {
            if(next != 0) {
                count += Long.bitCount(next);
            }
        }
        return count;
    }

    public int countFalse() {
        return size - countTrue();
    }

    public int firstTrue() {
        for(int i=0; i<data.length; i++) {
            long next = data[i];
            if(next != 0) {
                int index = Long.numberOfTrailingZeros(next);
                return (i << INDEX_BIT_SHIFT) + index;
            }
        }
        return -1;
    }

    public int getSize() {
        return size;
    }

    public FixedSizeBitSet not() {
        long[] newData = new long[data.length];
        for(int i=0; i<data.length; i++) {
            newData[i] = ~data[i];
        }
        return new FixedSizeBitSet(newData, getSize());
    }

    public FixedSizeBitSet resize(int newSize) {
        int longSize = data.length;
        int newLongSize = arraySize(newSize);
        long[] newData = new long[newSize];
        System.arraycopy(data, 0, newData, 0, newLongSize >= longSize
                ? data.length
                : newLongSize);
        return new FixedSizeBitSet(newData, newSize);
    }

    /**
     * Calculate the size of the set in longs, just a {@code ceil(size / Long.SIZE)}
     * @param setSize desired size of the set in bits
     * @return number of the longs to use
     */
    private static int arraySize(int setSize) {
        return 1 + ((setSize - 1) >> INDEX_BIT_SHIFT);
    }
}
