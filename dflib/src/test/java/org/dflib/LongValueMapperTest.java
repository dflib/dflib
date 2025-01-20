package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongValueMapperTest {

    @Deprecated
    @Test
    void fromObject() {

        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.fromObject().map(null));
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.fromObject().map(""));

        // these are different from "of()" that doesn't throw on booleans
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.fromObject().map(Boolean.TRUE));
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.fromObject().map(Boolean.FALSE));

        assertEquals(1L, LongValueMapper.fromObject().map(1L));
        assertEquals(-1L, LongValueMapper.fromObject().map(-1L));
        assertEquals(0L, LongValueMapper.fromObject().map(0L));

        assertEquals(100L, LongValueMapper.fromObject().map(100f));
        assertEquals(100L, LongValueMapper.fromObject().map(100d));
        assertEquals(100L, LongValueMapper.fromObject().map(100));

        assertEquals(1L, LongValueMapper.fromObject().map("1"));
        assertEquals(-1L, LongValueMapper.fromObject().map("-1"));
        assertEquals(0L, LongValueMapper.fromObject().map("0"));
    }

    @Test
    void of() {
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.of().map(null));
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.of().map(""));

        assertEquals(1L, LongValueMapper.of().map(Boolean.TRUE));
        assertEquals(0L, LongValueMapper.of().map(Boolean.FALSE));

        assertEquals(1L, LongValueMapper.of().map(1L));
        assertEquals(-1L, LongValueMapper.of().map(-1L));
        assertEquals(0L, LongValueMapper.of().map(0L));

        assertEquals(1L, LongValueMapper.of().map("1"));
        assertEquals(-1L, LongValueMapper.of().map("-1"));
        assertEquals(0, LongValueMapper.of().map("0"));
    }

    @Test
    void of_forNull() {
        assertEquals(11147483647L, LongValueMapper.of(11147483647L).map(null));
        assertEquals(11147483647L, LongValueMapper.of(11147483647L).map(""));

        assertEquals(1L, LongValueMapper.of(11147483647L).map(Boolean.TRUE));
        assertEquals(0L, LongValueMapper.of(11147483647L).map(Boolean.FALSE));

        assertEquals(1L, LongValueMapper.of(11147483647L).map(1L));
        assertEquals(-1L, LongValueMapper.of(11147483647L).map(-1L));
        assertEquals(0L, LongValueMapper.of(11147483647L).map(0L));

        assertEquals(1L, LongValueMapper.of(11147483647L).map("1"));
        assertEquals(-1L, LongValueMapper.of(11147483647L).map("-1"));
        assertEquals(0L, LongValueMapper.of(11147483647L).map("0"));
    }

    @Test
    void ofStr() {
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.ofStr().map(null));
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.ofStr().map(""));

        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.ofStr().map("true"));
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.ofStr().map("false"));

        assertEquals(1L, LongValueMapper.ofStr().map("1"));
        assertEquals(-1L, LongValueMapper.ofStr().map("-1"));
        assertEquals(0L, LongValueMapper.ofStr().map("0"));
    }

    @Test
    void ofStr_forNull() {
        assertEquals(11147483647L, LongValueMapper.ofStr(11147483647L).map(null));
        assertEquals(11147483647L, LongValueMapper.ofStr(11147483647L).map(""));

        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.ofStr(11147483647L).map("true"));
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.ofStr(11147483647L).map("false"));

        assertEquals(1L, LongValueMapper.ofStr(11147483647L).map("1"));
        assertEquals(10147483647L, LongValueMapper.ofStr(11147483647L).map("10147483647"));
        assertEquals(-1L, LongValueMapper.ofStr(11147483647L).map("-1"));
        assertEquals(0L, LongValueMapper.ofStr(11147483647L).map("0"));
    }

    @Test
    void ofNum() {
        assertThrows(IllegalArgumentException.class, () -> LongValueMapper.ofNum().map(null));

        assertEquals(1L, LongValueMapper.ofNum().map(1L));
        assertEquals(10147483647L, LongValueMapper.ofNum().map(10147483647L));
        assertEquals(-1L, LongValueMapper.ofNum().map(-1L));
        assertEquals(0L, LongValueMapper.ofNum().map(0L));
    }

    @Test
    void ofNum_forNull() {
        assertEquals(-100, LongValueMapper.ofNum(-100).map(null));

        assertEquals(1, LongValueMapper.ofNum(-100).map(1));
        assertEquals(10147483647L, LongValueMapper.ofNum(-100).map(10147483647L));
        assertEquals(-1, LongValueMapper.ofNum(-100).map(-1));
        assertEquals(0, LongValueMapper.ofNum(-100).map(0));
    }
}
