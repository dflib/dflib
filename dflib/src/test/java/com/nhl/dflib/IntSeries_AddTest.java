package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_AddTest {

    @Test
    public void testAdd() {
        Series<?> s = Series.ofInt(3, 28).add("abc");
        new SeriesAsserts(s).expectData(3, 28, "abc");
    }

    @Test
    public void testAddInt() {
        IntSeries s = Series.ofInt(3, 28).addInt(5);
        new IntSeriesAsserts(s).expectData(3, 28, 5);
    }

    @Test
    public void testAdd_Series() {
        IntSeries s0 = Series.ofInt(1, 2);
        IntSeries s = Series.ofInt(3, 28).add(s0);
        new SeriesAsserts(s).expectData(4, 30);
    }
}
