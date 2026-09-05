package org.dflib.parquet.read;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class LongKeyDictionaryTest {

    @Test
    public void missOnEmpty() {
        assertNull(new LongKeyDictionary().get(1));
    }

    @Test
    public void sharesOneInstancePerKey() {

        LongKeyDictionary d = new LongKeyDictionary();

        Object v1 = new Object();
        Object v2 = new Object();
        d.put(1, v1);
        d.put(2, v2);

        // identity is the whole point - the caller stores what comes back in place of decoding again
        assertSame(v1, d.get(1));
        assertSame(v1, d.get(1));
        assertSame(v2, d.get(2));
        assertNull(d.get(3));
    }

    @Test
    public void firstPutWins() {

        // a second decode of the same physical value produces an equal instance, and the dictionary keeps the one
        // already handed out, so that a column never mixes two instances of the same value

        LongKeyDictionary d = new LongKeyDictionary();

        Object first = new Object();
        d.put(7, first);
        d.put(7, new Object());

        assertSame(first, d.get(7));
    }

    @Test
    public void nullValueNotStored() {

        // a stored null would read back as an empty slot, so it is skipped - the key stays unknown

        LongKeyDictionary d = new LongKeyDictionary();
        d.put(1, null);
        assertNull(d.get(1));

        // ... and the slot is still free for a real value
        Object v = new Object();
        d.put(1, v);
        assertSame(v, d.get(1));
    }

    @Test
    public void edgeKeys() {

        // an empty slot is recognized from a null value, not from a zero key, so 0 must be an ordinary key
        long[] keys = {0L, -1L, 1L, Long.MIN_VALUE, Long.MAX_VALUE};

        LongKeyDictionary d = new LongKeyDictionary();
        Object[] values = new Object[keys.length];

        for (int i = 0; i < keys.length; i++) {
            values[i] = new Object();
            d.put(keys[i], values[i]);
        }

        for (int i = 0; i < keys.length; i++) {
            assertSame(values[i], d.get(keys[i]), "key " + keys[i]);
        }
    }

    @Test
    public void growsPreservingEntries() {

        // well past the initial capacity of 64, so the table is rehashed several times over, and far enough into
        // collision territory to exercise probing

        int size = 5000;

        LongKeyDictionary d = new LongKeyDictionary();
        Object[] values = new Object[size];

        for (int i = 0; i < size; i++) {
            values[i] = new Object();
            d.put(i, values[i]);
        }

        for (int i = 0; i < size; i++) {
            assertSame(values[i], d.get(i), "key " + i);
        }

        assertNull(d.get(size));
        assertNull(d.get(-1));
    }

    @Test
    public void growsPreservingEntries_SparseKeys() {

        // keys spread across the long range rather than packed into a dense low run, so the hash rather than the
        // key layout decides the slots

        int size = 2000;

        LongKeyDictionary d = new LongKeyDictionary();
        long[] keys = new long[size];
        Object[] values = new Object[size];

        for (int i = 0; i < size; i++) {
            // multiplying by an odd constant and rotating are both bijections on 64 bits, so the keys are distinct
            keys[i] = Long.rotateLeft((long) i * 0x9E3779B97F4A7C15L, 17);
            values[i] = new Object();
            d.put(keys[i], values[i]);
        }

        for (int i = 0; i < size; i++) {
            assertSame(values[i], d.get(keys[i]), "key " + keys[i]);
        }
    }
}
