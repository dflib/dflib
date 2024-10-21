package org.dflib.builder;

import org.dflib.series.BooleanBitsetSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoolBuilderTest {

    @Test
    void fillTrue1() {
        BoolBuilder builder = new BoolBuilder(i -> true, 1);

        BooleanBitsetSeries booleans = builder.buildSeries();

        assertEquals(1, booleans.size());
        assertTrue(booleans.get(0));
    }

    @Test
    void fillTrue65() {
        BoolBuilder builder = new BoolBuilder(i -> true, 65);

        BooleanBitsetSeries booleans = builder.buildSeries();

        assertEquals(65, booleans.size());
        assertTrue(booleans.get(0));
        assertTrue(booleans.get(63));
        assertTrue(booleans.get(64));
    }

    @Test
    void fillTrue128() {
        BoolBuilder builder = new BoolBuilder(i -> true, 128);

        BooleanBitsetSeries booleans = builder.buildSeries();

        assertEquals(128, booleans.size());
        assertTrue(booleans.get(0));
        assertTrue(booleans.get(63));
        assertTrue(booleans.get(64));
        assertTrue(booleans.get(127));
    }

    @Test
    void fillTrue129() {
        BoolBuilder builder = new BoolBuilder(i -> true, 129);

        BooleanBitsetSeries booleans = builder.buildSeries();

        assertEquals(129, booleans.size());
        assertTrue(booleans.get(0));
        assertTrue(booleans.get(63));
        assertTrue(booleans.get(64));
        assertTrue(booleans.get(127));
        assertTrue(booleans.get(128));
        assertFalse(booleans.get(129));
    }

}