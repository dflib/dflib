package org.dflib.parquet.read;

/**
 * A dictionary of decoded column values, keyed by the physical value each was decoded from, so that a value repeated
 * across rows is decoded once and shared by all of them.
 *
 * <p>Parquet's own dictionary is a dictionary of physical values: two rows share a dictionary entry exactly when
 * their physical bits are equal. And decoding is a pure function of those bits and the column's logical type, which
 * is fixed for the column. So the physical value addresses the dictionary just as well as the writer's entry index
 * would, and the loader can rebuild the mapping without ever seeing an index. Which it must, as Hardwood expands
 * numeric dictionaries during page decode and surfaces only the values (hardwood-hq/hardwood#513).
 *
 * <p>Open-addressed: a lookup is a probe over two flat arrays, with the key unboxed and no equality call on the
 * decoded value. An empty slot is one holding a null value, which decoders never produce - nulls are recognized from
 * the column's validity before a lookup happens.
 *
 * @since 2.0.0
 */
class LongKeyDictionary {

    private static final int INITIAL_CAPACITY = 64;

    private long[] keys;
    private Object[] values;
    private int mask;
    private int size;
    private int threshold;

    LongKeyDictionary() {
        this.keys = new long[INITIAL_CAPACITY];
        this.values = new Object[INITIAL_CAPACITY];
        this.mask = INITIAL_CAPACITY - 1;
        this.threshold = INITIAL_CAPACITY / 2;
    }

    /**
     * Returns the value already decoded for this physical value, or null if it hasn't been seen yet. A null answer
     * is an invitation to decode and {@link #put(long, Object)} the result.
     */
    Object get(long key) {
        return values[slot(key)];
    }

    void put(long key, Object value) {

        // a null would read back as an empty slot, so it is simply not stored - the value gets decoded again, which
        // is correct, just not shared
        if (value == null) {
            return;
        }

        int i = slot(key);
        if (values[i] == null) {
            keys[i] = key;
            values[i] = value;

            if (++size > threshold) {
                grow();
            }
        }
    }

    private int slot(long key) {
        int i = hash(key) & mask;
        while (values[i] != null && keys[i] != key) {
            i = (i + 1) & mask;
        }
        return i;
    }

    private void grow() {

        long[] oldKeys = keys;
        Object[] oldValues = values;

        int capacity = oldValues.length << 1;
        this.keys = new long[capacity];
        this.values = new Object[capacity];
        this.mask = capacity - 1;
        this.threshold = capacity / 2;

        for (int i = 0; i < oldValues.length; i++) {
            if (oldValues[i] != null) {
                int j = slot(oldKeys[i]);
                keys[j] = oldKeys[i];
                values[j] = oldValues[i];
            }
        }
    }

    private static int hash(long key) {
        long h = key * 0x9E3779B97F4A7C15L;
        return (int) (h ^ (h >>> 32));
    }
}
