package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntSeries_AggTest {

    @Test
    public void testSumInt() {
        IntSeries s = new IntArraySeries(1, 2);
        assertEquals(Integer.valueOf(3), SeriesAggregator.sumInt().aggregate(s));
    }

    @Test
    public void testSumLong() {
        IntSeries s = new IntArraySeries(1, 2);
        assertEquals(Long.valueOf(3), SeriesAggregator.sumLong().aggregate(s));
    }

    @Test
    public void testSumDouble() {
        IntSeries s = new IntArraySeries(1, 2);
        assertEquals(Double.valueOf(3.), SeriesAggregator.sumDouble().aggregate(s));
    }

    @Test
    public void testCountInt() {
        IntSeries s = new IntArraySeries(1, 2);
        assertEquals(Integer.valueOf(2), SeriesAggregator.countInt().aggregate(s));
    }

    @Test
    public void testCountLong() {
        IntSeries s = new IntArraySeries(1, 2);
        assertEquals(Long.valueOf(2), SeriesAggregator.countLong().aggregate(s));
    }
}
