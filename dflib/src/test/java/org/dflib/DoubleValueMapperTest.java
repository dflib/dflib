package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleValueMapperTest {

    @Deprecated
    @Test
    void fromObject() {

        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.fromObject().map(null));
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.fromObject().map(""));

        // these are different from "of()" that doesn't throw on booleans
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.fromObject().map(Boolean.TRUE));
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.fromObject().map(Boolean.FALSE));

        assertEquals(1.1, DoubleValueMapper.fromObject().map(1.1));
        assertEquals(-1.2, DoubleValueMapper.fromObject().map(-1.2));
        assertEquals(0., DoubleValueMapper.fromObject().map(0.));

        assertEquals(100d, DoubleValueMapper.fromObject().map(100L));
        assertEquals(100d, DoubleValueMapper.fromObject().map(100F));
        assertEquals(100d, DoubleValueMapper.fromObject().map(100));

        assertEquals(1., DoubleValueMapper.fromObject().map("1"));
        assertEquals(-1., DoubleValueMapper.fromObject().map("-1"));
        assertEquals(0., DoubleValueMapper.fromObject().map("0"));
    }


    @Test
    void of() {
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.of().map(null));
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.of().map(""));

        assertEquals(1., DoubleValueMapper.of().map(Boolean.TRUE));
        assertEquals(0., DoubleValueMapper.of().map(Boolean.FALSE));

        assertEquals(1.1, DoubleValueMapper.of().map(1.1));
        assertEquals(-1.2, DoubleValueMapper.of().map(-1.2));
        assertEquals(0., DoubleValueMapper.of().map(0.));

        assertEquals(1.1, DoubleValueMapper.of().map("1.1"));
        assertEquals(-1.2, DoubleValueMapper.of().map("-1.2"));
        assertEquals(0., DoubleValueMapper.of().map("0"));
    }

    @Test
    void of_forNull() {
        assertEquals(-0.5, DoubleValueMapper.of(-0.5).map(null));
        assertEquals(-0.5, DoubleValueMapper.of(-0.5).map(""));

        assertEquals(1., DoubleValueMapper.of(-0.5).map(Boolean.TRUE));
        assertEquals(0., DoubleValueMapper.of(-0.5).map(Boolean.FALSE));

        assertEquals(1.1, DoubleValueMapper.of(-0.5).map(1.1));
        assertEquals(-1.2, DoubleValueMapper.of(-0.5).map(-1.2));
        assertEquals(0., DoubleValueMapper.of(-0.5).map(0.));

        assertEquals(1.1, DoubleValueMapper.of(-0.5).map("1.1"));
        assertEquals(-1.2, DoubleValueMapper.of(-0.5).map("-1.2"));
        assertEquals(0., DoubleValueMapper.of(-0.5).map("0"));
    }

    @Test
    void ofStr() {
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.ofStr().map(null));
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.ofStr().map(""));

        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.ofStr().map("true"));
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.ofStr().map("false"));

        assertEquals(1.1d, DoubleValueMapper.ofStr().map("1.1"));
        assertEquals(-1.2d, DoubleValueMapper.ofStr().map("-1.2"));
        assertEquals(0d, DoubleValueMapper.ofStr().map("0"));
    }

    @Test
    void ofStr_forNull() {
        assertEquals(-0.5, DoubleValueMapper.ofStr(-0.5).map(null));
        assertEquals(-0.5, DoubleValueMapper.ofStr(-0.5).map(""));

        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.ofStr(-0.5).map("true"));
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.ofStr(-0.5).map("false"));

        assertEquals(1.1, DoubleValueMapper.ofStr(-0.5).map("1.1"));
        assertEquals(-1.2, DoubleValueMapper.ofStr(-0.5).map("-1.2"));
        assertEquals(0., DoubleValueMapper.ofStr(-0.5).map("0"));
    }

    @Test
    void ofNum() {
        assertThrows(IllegalArgumentException.class, () -> DoubleValueMapper.ofNum().map(null));

        assertEquals(1.1d, DoubleValueMapper.ofNum().map(1.1d), 0.00001);
        assertEquals(10147483647.1, LongValueMapper.ofNum().map(10147483647.1), 1.0);
        assertEquals(-1.2d, DoubleValueMapper.ofNum().map(-1.2), 0.0000001);
        assertEquals(0d, DoubleValueMapper.ofNum().map(0.f));

        assertEquals(100f, DoubleValueMapper.ofNum().map(100L));
        assertEquals(100f, DoubleValueMapper.ofNum().map(100d));
        assertEquals(100f, DoubleValueMapper.ofNum().map(100));
    }

    @Test
    void ofNum_forNull() {
        assertEquals(-100.1d, DoubleValueMapper.ofNum(-100.1d).map(null));

        assertEquals(1., DoubleValueMapper.ofNum(-100.1d).map(1));
        assertEquals(10147483647.1, DoubleValueMapper.ofNum(-100.1d).map(10147483647.1), 1.0);
        assertEquals(-1.2, DoubleValueMapper.ofNum(-100.1d).map(-1.2), 0.0000001);
        assertEquals(0., DoubleValueMapper.ofNum(-100.1d).map(0.));

        assertEquals(100d, DoubleValueMapper.ofNum(-100.1d).map(100L));
        assertEquals(100d, DoubleValueMapper.ofNum(-100.1d).map(100f));
        assertEquals(100d, DoubleValueMapper.ofNum(-100.1d).map(100));
    }
}
