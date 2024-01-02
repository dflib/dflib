package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_TailTest {

    @Test
    public void test() {
        DoubleSeries s = Series.ofDouble(3, 4, 2).tail(2);
        new DoubleSeriesAsserts(s).expectData(4, 2);
    }

    @Test
    public void zero() {
        DoubleSeries s = Series.ofDouble(3, 4, 2).tail(0);
        new DoubleSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        DoubleSeries s = Series.ofDouble(3, 4, 2).tail(4);
        new DoubleSeriesAsserts(s).expectData(3, 4, 2);
    }

    @Test
    public void negative() {
        DoubleSeries s = Series.ofDouble(3, 4, 2).tail(-2);
        new DoubleSeriesAsserts(s).expectData(3);
    }
}
