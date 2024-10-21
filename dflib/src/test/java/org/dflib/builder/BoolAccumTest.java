package org.dflib.builder;

import org.dflib.BooleanSeries;
import org.dflib.series.BooleanBitsetSeries;
import org.dflib.series.TrueSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoolAccumTest {

    @Test
    void createEmpty() {
        BoolAccum accum = new BoolAccum();
        BooleanBitsetSeries series = accum.toSeries();
        assertEquals(0, series.size());
    }

    @Test
    void createWithCapacity() {
        BoolAccum accum = new BoolAccum(10);
        BooleanBitsetSeries series = accum.toSeries();
        assertEquals(0, series.size());
    }

    @Test
    void create1() {
        BoolAccum accum = new BoolAccum();
        accum.pushBool(true);
        BooleanBitsetSeries series = accum.toSeries();

        assertEquals(1, series.size());
        assertTrue(series.get(0));
    }

    @Test
    void create65() {
        BoolAccum accum = new BoolAccum();
        for(int i = 0; i < 65; i++) {
            accum.pushBool(true);
        }
        BooleanBitsetSeries series = accum.toSeries();

        assertEquals(65, series.size());
        assertTrue(series.get(0));
        assertTrue(series.get(1));
        assertTrue(series.get(64));
        assertEquals(0, series.countFalse());
        assertEquals(65, series.countTrue());
    }

    @Test
    void fill() {
        BoolAccum accum = new BoolAccum();
        accum.fill(1, 66, true);
        BooleanBitsetSeries series = accum.toSeries();

        assertEquals(66, series.size());
        assertFalse(series.get(0));
        assertTrue(series.get(1));
        assertTrue(series.get(65));
        assertEquals(1, series.countFalse());
        assertEquals(65, series.countTrue());
    }

    @Test
    void fillFromSeries() {
        BoolAccum accum = new BoolAccum();
        BooleanSeries trueSeries = new TrueSeries(129);
        accum.pushBool(false);
        accum.pushBool(true);
        accum.fill(trueSeries, 0, 10, trueSeries.size());
        BooleanBitsetSeries series = accum.toSeries();

        assertEquals(139, series.size());
        assertFalse(series.get(0));
        assertTrue(series.get(1));
        assertTrue(series.get(138));
        assertEquals(9, series.countFalse());
        assertEquals(130, series.countTrue());
    }

    @Test
    void replaceFalse() {
        BoolAccum accum = new BoolAccum();
        accum.pushBool(true);
        accum.replace(0, false);
        BooleanBitsetSeries series = accum.toSeries();

        assertEquals(1, series.size());
        assertFalse(series.get(0));
    }

    @Test
    void replaceTrue() {
        BoolAccum accum = new BoolAccum();
        accum.pushBool(false);
        accum.replace(0, true);
        BooleanBitsetSeries series = accum.toSeries();

        assertEquals(1, series.size());
        assertTrue(series.get(0));
    }

}