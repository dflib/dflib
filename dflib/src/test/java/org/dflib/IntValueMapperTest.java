package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntValueMapperTest {

    @Deprecated
    @Test
    void fromObject() {

        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.fromObject().map(null));
        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.fromObject().map(""));

        assertEquals(1, IntValueMapper.fromObject().map(Boolean.TRUE));
        assertEquals(0, IntValueMapper.fromObject().map(Boolean.FALSE));

        assertEquals(1, IntValueMapper.fromObject().map(1));
        assertEquals(-1, IntValueMapper.fromObject().map(-1));
        assertEquals(0, IntValueMapper.fromObject().map(0));

        assertEquals(1, IntValueMapper.fromObject().map("1"));
        assertEquals(-1, IntValueMapper.fromObject().map("-1"));
        assertEquals(0, IntValueMapper.fromObject().map("0"));
    }

    @Test
    void of() {

        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.of().map(null));
        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.of().map(""));

        assertEquals(1, IntValueMapper.of().map(Boolean.TRUE));
        assertEquals(0, IntValueMapper.of().map(Boolean.FALSE));

        assertEquals(1, IntValueMapper.of().map(1));
        assertEquals(-1, IntValueMapper.of().map(-1));
        assertEquals(0, IntValueMapper.of().map(0));

        assertEquals(1, IntValueMapper.of().map("1"));
        assertEquals(-1, IntValueMapper.of().map("-1"));
        assertEquals(0, IntValueMapper.of().map("0"));
    }

    @Test
    void of_forNull() {
        assertEquals(-100, IntValueMapper.of(-100).map(null));
        assertEquals(-100, IntValueMapper.of(-100).map(""));

        assertEquals(1, IntValueMapper.of(-100).map(Boolean.TRUE));
        assertEquals(0, IntValueMapper.of(-100).map(Boolean.FALSE));

        assertEquals(1, IntValueMapper.of(-100).map(1));
        assertEquals(-1, IntValueMapper.of(-100).map(-1));
        assertEquals(0, IntValueMapper.of(-100).map(0));

        assertEquals(1, IntValueMapper.of(-100).map("1"));
        assertEquals(-1, IntValueMapper.of(-100).map("-1"));
        assertEquals(0, IntValueMapper.of(-100).map("0"));
    }

    @Test
    void ofStr() {
        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.ofStr().map(null));
        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.ofStr().map(""));

        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.ofStr().map("true"));
        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.ofStr().map("false"));

        assertEquals(1, IntValueMapper.ofStr().map("1"));
        assertEquals(-1, IntValueMapper.ofStr().map("-1"));
        assertEquals(0, IntValueMapper.ofStr().map("0"));
    }

    @Test
    void ofStr_forNull() {
        assertEquals(-100, IntValueMapper.ofStr(-100).map(null));
        assertEquals(-100, IntValueMapper.ofStr(-100).map(""));

        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.ofStr(-100).map("true"));
        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.ofStr(-100).map("false"));

        assertEquals(1, IntValueMapper.ofStr(-100).map("1"));
        assertEquals(-1, IntValueMapper.ofStr(-100).map("-1"));
        assertEquals(0, IntValueMapper.ofStr(-100).map("0"));
    }

    @Test
    void ofNum() {
        assertThrows(IllegalArgumentException.class, () -> IntValueMapper.ofNum().map(null));

        assertEquals(1, IntValueMapper.ofNum().map(1));
        assertEquals(-1, IntValueMapper.ofNum().map(-1));
        assertEquals(0, IntValueMapper.ofNum().map(0));
    }

    @Test
    void ofNum_forNull() {
        assertEquals(-100, IntValueMapper.ofNum(-100).map(null));

        assertEquals(1, IntValueMapper.ofNum(-100).map(1));
        assertEquals(-1, IntValueMapper.ofNum(-100).map(-1));
        assertEquals(0, IntValueMapper.ofNum(-100).map(0));
    }
}
