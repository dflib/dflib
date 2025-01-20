package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FloatValueMapperTest {

    @Deprecated
    @Test
    void fromObject() {

        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.fromObject().map(null));
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.fromObject().map(""));

        // these are different from "of()" that doesn't throw on booleans
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.fromObject().map(Boolean.TRUE));
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.fromObject().map(Boolean.FALSE));

        assertEquals(1.1f, FloatValueMapper.fromObject().map(1.1f));
        assertEquals(-1.2f, FloatValueMapper.fromObject().map(-1.2f));
        assertEquals(0.f, FloatValueMapper.fromObject().map(0.f));

        assertEquals(100f, FloatValueMapper.fromObject().map(100L));
        assertEquals(100f, FloatValueMapper.fromObject().map(100d));
        assertEquals(100f, FloatValueMapper.fromObject().map(100));

        assertEquals(1f, FloatValueMapper.fromObject().map("1"));
        assertEquals(-1f, FloatValueMapper.fromObject().map("-1"));
        assertEquals(0f, FloatValueMapper.fromObject().map("0"));
    }


    @Test
    void of() {
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.of().map(null));
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.of().map(""));

        assertEquals(1.f, FloatValueMapper.of().map(Boolean.TRUE));
        assertEquals(0.f, FloatValueMapper.of().map(Boolean.FALSE));

        assertEquals(1.1f, FloatValueMapper.of().map(1.1f));
        assertEquals(-1.2f, FloatValueMapper.of().map(-1.2f));
        assertEquals(0.f, FloatValueMapper.of().map(0.f));

        assertEquals(100f, FloatValueMapper.of().map(100L));
        assertEquals(100f, FloatValueMapper.of().map(100d));
        assertEquals(100f, FloatValueMapper.of().map(100));

        assertEquals(1.1f, FloatValueMapper.of().map("1.1"));
        assertEquals(-1.2f, FloatValueMapper.of().map("-1.2"));
        assertEquals(0.f, FloatValueMapper.of().map("0"));
    }

    @Test
    void of_forNull() {
        assertEquals(-0.5f, FloatValueMapper.of(-0.5f).map(null));
        assertEquals(-0.5f, FloatValueMapper.of(-0.5f).map(""));

        assertEquals(1.f, FloatValueMapper.of(-0.5f).map(Boolean.TRUE));
        assertEquals(0.f, FloatValueMapper.of(-0.5f).map(Boolean.FALSE));

        assertEquals(1.1f, FloatValueMapper.of(-0.5f).map(1.1f));
        assertEquals(-1.2f, FloatValueMapper.of(-0.5f).map(-1.2f));
        assertEquals(0.f, FloatValueMapper.of(-0.5f).map(0.f));

        assertEquals(1.1f, FloatValueMapper.of(-0.5f).map("1.1"));
        assertEquals(-1.2f, FloatValueMapper.of(-0.5f).map("-1.2"));
        assertEquals(0.f, FloatValueMapper.of(-0.5f).map("0"));
    }

    @Test
    void ofStr() {
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.ofStr().map(null));
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.ofStr().map(""));

        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.ofStr().map("true"));
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.ofStr().map("false"));

        assertEquals(1f, FloatValueMapper.ofStr().map("1"));
        assertEquals(-1f, FloatValueMapper.ofStr().map("-1"));
        assertEquals(0f, FloatValueMapper.ofStr().map("0"));
    }

    @Test
    void ofStr_forNull() {
        assertEquals(-0.5f, FloatValueMapper.ofStr(-0.5f).map(null));
        assertEquals(-0.5f, FloatValueMapper.ofStr(-0.5f).map(""));

        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.ofStr(-0.5f).map("true"));
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.ofStr(-0.5f).map("false"));

        assertEquals(1.1f, FloatValueMapper.ofStr(-0.5f).map("1.1"));
        assertEquals(-1.2f, FloatValueMapper.ofStr(-0.5f).map("-1.2"));
        assertEquals(0.f, FloatValueMapper.ofStr(-0.5f).map("0"));
    }

    @Test
    void ofNum() {
        assertThrows(IllegalArgumentException.class, () -> FloatValueMapper.ofNum().map(null));

        assertEquals(1.1f, FloatValueMapper.ofNum().map(1.1f));
        assertEquals(-1.2f, FloatValueMapper.ofNum().map(-1.2));
        assertEquals(0f, FloatValueMapper.ofNum().map(0.f));

        assertEquals(100f, FloatValueMapper.ofNum().map(100L));
        assertEquals(100f, FloatValueMapper.ofNum().map(100d));
        assertEquals(100f, FloatValueMapper.ofNum().map(100));
    }

    @Test
    void ofNum_forNull() {
        assertEquals(-100.1f, FloatValueMapper.ofNum(-100.1f).map(null));

        assertEquals(1, FloatValueMapper.ofNum(-100.1f).map(1));
        assertEquals(-1, FloatValueMapper.ofNum(-100.1f).map(-1));
        assertEquals(0, FloatValueMapper.ofNum(-100.1f).map(0));

        assertEquals(100f, FloatValueMapper.ofNum(-100.1f).map(100L));
        assertEquals(100f, FloatValueMapper.ofNum(-100.1f).map(100d));
        assertEquals(100f, FloatValueMapper.ofNum(-100.1f).map(100));
    }
}
