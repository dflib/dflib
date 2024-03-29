package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntSeries_PositionTest {

    @Test
    public void position() {
        assertEquals(-1, Series.ofInt(3, 4, 2).position(null));
        assertEquals(1, Series.ofInt(3, 4, 2).position(4));
        assertEquals(-1, Series.ofInt(3, 4, 2).position(5));
    }
}
