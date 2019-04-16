package com.nhl.dflib;

import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.unit.DoubleSeriesAsserts;
import org.junit.Test;

public class DoubleSeries_SelectDoubleTest {

    @Test
    public void test() {
        DoubleSeries s = new DoubleArraySeries(3, 4, 2).selectDouble(2, 1);
        new DoubleSeriesAsserts(s).expectData(2, 4);
    }

    @Test
    public void test_Empty() {
        DoubleSeries s = new DoubleArraySeries(3, 4, 2).selectDouble();
        new DoubleSeriesAsserts(s).expectData();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test_OutOfBounds() {
        DoubleSeries s = new DoubleArraySeries(3, 4, 2).selectDouble(0, 3);
        new DoubleSeriesAsserts(s);
    }
}
