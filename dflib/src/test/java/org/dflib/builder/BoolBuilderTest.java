package org.dflib.builder;

import org.dflib.BooleanSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoolBuilderTest {

    @Test
    void fillTrue1() {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> true, 1);

        assertEquals(1, booleans.size());
        assertTrue(booleans.get(0));
    }

    @Test
    void fillTrue64() {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> true, 64);

        assertEquals(64, booleans.size());
        assertTrue(booleans.get(0));
        assertTrue(booleans.get(62));
        assertTrue(booleans.get(63));
    }

    @Test
    void fillTrue65() {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> true, 65);

        assertEquals(65, booleans.size());
        assertTrue(booleans.get(0));
        assertTrue(booleans.get(63));
        assertTrue(booleans.get(64));
    }

    @Test
    void fillTrue128() {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> true, 128);

        assertEquals(128, booleans.size());
        assertTrue(booleans.get(0));
        assertTrue(booleans.get(63));
        assertTrue(booleans.get(64));
        assertTrue(booleans.get(127));
    }

    @Test
    void fillTrue129() {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> true, 129);

        assertEquals(129, booleans.size());
        assertTrue(booleans.get(0));
        assertTrue(booleans.get(63));
        assertTrue(booleans.get(64));
        assertTrue(booleans.get(127));
        assertTrue(booleans.get(128));
    }

    @Test
    void fillFalse1() {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> false, 1);

        assertEquals(1, booleans.size());
        assertFalse(booleans.get(0));
    }

    @Test
    void fillFalse10() {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> false, 10);

        assertEquals(10, booleans.size());
        assertFalse(booleans.get(0));
        assertFalse(booleans.get(9));
    }

    @Test
    void fillFalseFirst10() {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> i >= 10, 20);

        assertEquals(20, booleans.size());
        assertFalse(booleans.get(0));
        assertFalse(booleans.get(9));
        assertTrue(booleans.get(11));
        assertTrue(booleans.get(19));
    }
}