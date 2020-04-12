package com.nhl.dflib;

import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_HeadDoubleTest {

    @Test
    public void test() {
        DoubleSeries s = new DoubleArraySeries(3, 4, 2).headDouble(2);
        new DoubleSeriesAsserts(s).expectData(3, 4);
    }

    @Test
    public void test_Zero() {
        DoubleSeries s = new DoubleArraySeries(3, 4, 2).headDouble(0);
        new DoubleSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        DoubleSeries s = new DoubleArraySeries(3, 4, 2).headDouble(4);
        new DoubleSeriesAsserts(s).expectData(3, 4, 2);
    }
}
