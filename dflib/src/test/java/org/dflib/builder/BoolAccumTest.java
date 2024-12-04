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
        BooleanSeries series = accum.toSeries();
        assertEquals(0, series.size());
    }

    @Test
    void createWithCapacity() {
        BoolAccum accum = new BoolAccum(10);
        BooleanSeries series = accum.toSeries();
        assertEquals(0, series.size());
    }

    @Test
    void compact() {
        BoolAccum accum = new BoolAccum(300);
        accum.pushBool(true);
        BooleanSeries series = accum.toSeries();
        assertInstanceOf(BooleanBitsetSeries.class, series);
        assertEquals(1, series.size());
        assertTrue(series.get(0));
    }

    @Test
    void compact2() {
        BoolAccum accum = new BoolAccum(300);
        for (int i = 0; i < Long.SIZE + 1; i++) {
            accum.pushBool(true);
        }
        BooleanSeries series = accum.toSeries();
        assertInstanceOf(BooleanBitsetSeries.class, series);
        assertEquals(Long.SIZE + 1, series.size());
        assertTrue(series.get(0));
        assertTrue(series.get(Long.SIZE));
    }

    @Test
    void create1() {
        BoolAccum accum = new BoolAccum();
        accum.pushBool(true);
        BooleanSeries series = accum.toSeries();

        assertEquals(1, series.size());
        assertTrue(series.get(0));
    }

    @Test
    void create65() {
        BoolAccum accum = new BoolAccum();
        for (int i = 0; i < 65; i++) {
            accum.pushBool(true);
        }
        BooleanSeries series = accum.toSeries();

        assertEquals(65, series.size());
        assertTrue(series.get(0));
        assertTrue(series.get(1));
        assertTrue(series.get(64));
        assertEquals(0, series.countFalse());
        assertEquals(65, series.countTrue());
    }

    @Test
    void push() {
        BoolAccum accum = new BoolAccum();
        accum.push(true);
        accum.push(false);
        accum.push(false);
        accum.push(true);
        accum.push(true);
        accum.push(true);
        accum.push(false);
        accum.push(false);

        BooleanSeries series = accum.toSeries();
        assertEquals(8, series.size());
        assertTrue(series.get(0));
        assertFalse(series.get(1));
        assertFalse(series.get(2));
        assertTrue(series.get(3));
        assertTrue(series.get(4));
        assertTrue(series.get(5));
        assertFalse(series.get(6));
        assertFalse(series.get(7));
    }


    @Test
    void fill() {
        BoolAccum accum = new BoolAccum();
        accum.fill(1, 66, true);
        BooleanSeries series = accum.toSeries();

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
        BooleanSeries series = accum.toSeries();

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
        BooleanSeries series = accum.toSeries();

        assertEquals(1, series.size());
        assertFalse(series.get(0));
    }

    @Test
    void replaceTrue() {
        BoolAccum accum = new BoolAccum();
        accum.pushBool(false);
        accum.replace(0, true);
        BooleanSeries series = accum.toSeries();

        assertEquals(1, series.size());
        assertTrue(series.get(0));
    }

}