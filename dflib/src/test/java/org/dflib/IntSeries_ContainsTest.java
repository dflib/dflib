package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntSeries_ContainsTest {

    @Test
    public void contains() {
        assertFalse(Series.ofInt(3, 4, 2).contains(null));
        assertTrue(Series.ofInt(3, 4, 2).contains(4));
        assertFalse(Series.ofInt(3, 4, 2).contains(5));
    }
}
