package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;

public class IntSeries_SelectIntTest {

    @Test
    public void test() {
        IntSeries s = new IntArraySeries(3, 4, 2).selectInt(2, 1);
        new IntSeriesAsserts(s).expectData(2, 4);
    }

    @Test
    public void test_Empty() {
        IntSeries s = new IntArraySeries(3, 4, 2).selectInt();
        new IntSeriesAsserts(s).expectData();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test_OutOfBounds() {
        IntSeries s = new IntArraySeries(3, 4, 2).selectInt(0, 3);
        new IntSeriesAsserts(s);
    }
}
