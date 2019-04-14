package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;

public class IntSeries_MatchingPositionsIntTest {

    @Test
    public void test() {
        IntSeries s = new IntArraySeries(3, 4, 2).indexInt(i -> i % 2 == 0);
        new IntSeriesAsserts(s).expectData(1, 2);
    }

    @Test
    public void test_All() {
        IntSeries s = new IntArraySeries(3, 4, 2).indexInt(i -> true);
        new IntSeriesAsserts(s).expectData(0, 1, 2);
    }

    @Test
    public void test_None() {
        IntSeries s = new IntArraySeries(3, 4, 2).indexInt(i -> false);
        new IntSeriesAsserts(s).expectData();
    }
}
