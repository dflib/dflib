package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntSeries_MapTest {

    @Test
    public void testMap_Exp() {
        Series<? extends Number> s = Series.ofInt(3, 28).map(Exp.$int(0).add(10));
        new SeriesAsserts(s).expectData(13, 38);
    }

    @Test
    public void testMapAsInt() {
        Series<Integer> s = Series.ofInt(3, 28).mapAsInt(i -> i + 10);
        assertTrue(s instanceof IntSeries);
        new IntSeriesAsserts((IntSeries) s).expectData(13, 38);
    }
}
