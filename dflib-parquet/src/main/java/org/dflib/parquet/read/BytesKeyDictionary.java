package org.dflib.parquet.read;

import java.util.Arrays;

/**
 * A dictionary of decoded column values, keyed by the bytes each was decoded from. The byte-array counterpart of
 * {@link LongKeyDictionary}, and the same idea: the physical value is the dictionary key, so a value repeated across
 * rows is decoded once and shared by all of them.
 *
 * <p>Keys are read straight out of the batch's shared byte buffer, and only a distinct one is ever copied - so a
 * lookup that hits allocates nothing at all, and the copies add up to the dictionary rather than to the column.
 *
 * @since 2.0.0
 */
class BytesKeyDictionary {

    private static final int INITIAL_CAPACITY = 64;

    private byte[][] keys;
    private Object[] values;
    private int mask;
    private int size;
    private int threshold;

    BytesKeyDictionary() {
        this.keys = new byte[INITIAL_CAPACITY][];
        this.values = new Object[INITIAL_CAPACITY];
        this.mask = INITIAL_CAPACITY - 1;
        this.threshold = INITIAL_CAPACITY / 2;
    }

    /**
     * Returns the value already decoded for these bytes, or null if they haven't been seen yet. A null answer is an
     * invitation to decode and {@link #put(byte[], int, int, Object)} the result.
     */
    Object get(byte[] buffer, int offset, int len) {
        return values[slot(buffer, offset, len)];
    }

    void put(byte[] buffer, int offset, int len, Object value) {

        // a null would read back as an empty slot, so it is simply not stored - the value gets decoded again, which
        // is correct, just not shared
        if (value == null) {
            return;
        }

        int i = slot(buffer, offset, len);
        if (values[i] == null) {
            keys[i] = Arrays.copyOfRange(buffer, offset, offset + len);
            values[i] = value;

            if (++size > threshold) {
                grow();
            }
        }
    }

    private int slot(byte[] buffer, int offset, int len) {
        int i = hash(buffer, offset, len) & mask;
        while (values[i] != null && !Arrays.equals(keys[i], 0, keys[i].length, buffer, offset, offset + len)) {
            i = (i + 1) & mask;
        }
        return i;
    }

    private void grow() {

        byte[][] oldKeys = keys;
        Object[] oldValues = values;

        int capacity = oldValues.length << 1;
        this.keys = new byte[capacity][];
        this.values = new Object[capacity];
        this.mask = capacity - 1;
        this.threshold = capacity / 2;

        for (int i = 0; i < oldValues.length; i++) {
            if (oldValues[i] != null) {
                byte[] key = oldKeys[i];
                int j = slot(key, 0, key.length);
                keys[j] = key;
                values[j] = oldValues[i];
            }
        }
    }

    private static int hash(byte[] buffer, int offset, int len) {
        int h = 1;
        int end = offset + len;
        for (int i = offset; i < end; i++) {
            h = 31 * h + buffer[i];
        }
        return h ^ (h >>> 16);
    }
}
