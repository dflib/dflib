package org.dflib.parquet.read;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class BytesKeyDictionaryTest {

    @Test
    public void missOnEmpty() {
        assertNull(new BytesKeyDictionary().get(new byte[]{1, 2}, 0, 2));
    }

    @Test
    public void sharesOneInstancePerKey() {

        BytesKeyDictionary d = new BytesKeyDictionary();

        Object v1 = new Object();
        Object v2 = new Object();
        d.put(new byte[]{1, 2}, 0, 2, v1);
        d.put(new byte[]{3, 4}, 0, 2, v2);

        assertSame(v1, d.get(new byte[]{1, 2}, 0, 2));
        assertSame(v1, d.get(new byte[]{1, 2}, 0, 2));
        assertSame(v2, d.get(new byte[]{3, 4}, 0, 2));
        assertNull(d.get(new byte[]{5, 6}, 0, 2));
    }

    @Test
    public void keyIsTheRangeNotTheArray() {

        // values are read out of one shared batch buffer, so the same bytes at a different offset are the same key,
        // and a different range of the same array is a different one

        byte[] buffer = {1, 2, 3, 1, 2};

        BytesKeyDictionary d = new BytesKeyDictionary();

        Object v12 = new Object();
        d.put(buffer, 0, 2, v12);

        assertSame(v12, d.get(buffer, 3, 2));
        assertNull(d.get(buffer, 1, 2));

        Object v23 = new Object();
        d.put(buffer, 1, 2, v23);

        assertSame(v23, d.get(buffer, 1, 2));
        assertSame(v12, d.get(buffer, 0, 2));
        assertSame(v12, d.get(buffer, 3, 2));
    }

    @Test
    public void keyIsCopiedOutOfTheBuffer() {

        // Hardwood hands out one batch buffer and refills it, so a key that aliased it would start matching whatever
        // landed there next

        byte[] buffer = {1, 2, 3, 4};

        BytesKeyDictionary d = new BytesKeyDictionary();
        Object v = new Object();
        d.put(buffer, 0, 4, v);

        Arrays.fill(buffer, (byte) 0);

        assertSame(v, d.get(new byte[]{1, 2, 3, 4}, 0, 4));
        assertNull(d.get(buffer, 0, 4));
    }

    @Test
    public void distinguishesByLength() {

        byte[] buffer = {1, 2, 3};

        BytesKeyDictionary d = new BytesKeyDictionary();

        Object shortKey = new Object();
        Object longKey = new Object();
        d.put(buffer, 0, 2, shortKey);
        d.put(buffer, 0, 3, longKey);

        assertSame(shortKey, d.get(buffer, 0, 2));
        assertSame(longKey, d.get(buffer, 0, 3));
    }

    @Test
    public void emptyKey() {

        // a variable-length BYTE_ARRAY column can hold a zero-length value

        byte[] buffer = {1, 2, 3};

        BytesKeyDictionary d = new BytesKeyDictionary();
        Object empty = new Object();
        d.put(buffer, 0, 0, empty);

        assertSame(empty, d.get(buffer, 0, 0));
        assertSame(empty, d.get(buffer, 2, 0));
        assertNull(d.get(buffer, 0, 1));
    }

    @Test
    public void firstPutWins() {

        BytesKeyDictionary d = new BytesKeyDictionary();

        Object first = new Object();
        d.put(new byte[]{9}, 0, 1, first);
        d.put(new byte[]{9}, 0, 1, new Object());

        assertSame(first, d.get(new byte[]{9}, 0, 1));
    }

    @Test
    public void nullValueNotStored() {

        BytesKeyDictionary d = new BytesKeyDictionary();
        d.put(new byte[]{1}, 0, 1, null);
        assertNull(d.get(new byte[]{1}, 0, 1));

        Object v = new Object();
        d.put(new byte[]{1}, 0, 1, v);
        assertSame(v, d.get(new byte[]{1}, 0, 1));
    }

    @Test
    public void growsPreservingEntries() {

        // well past the initial capacity of 64, so the table is rehashed several times over

        int size = 5000;

        BytesKeyDictionary d = new BytesKeyDictionary();
        Object[] values = new Object[size];

        for (int i = 0; i < size; i++) {
            values[i] = new Object();
            d.put(key(i), 0, 4, values[i]);
        }

        for (int i = 0; i < size; i++) {
            assertSame(values[i], d.get(key(i), 0, 4), "key " + i);
        }

        assertNull(d.get(key(size), 0, 4));
    }

    private static byte[] key(int i) {
        return new byte[]{(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i};
    }
}
