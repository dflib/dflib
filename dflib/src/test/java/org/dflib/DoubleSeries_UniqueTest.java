package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_UniqueTest {

    @Test
    public void test() {
        DoubleSeries s1 = Series.ofDouble(0., -1.1, -1.1, 0., 1.1, 375.05, Double.MAX_VALUE, 5.1, Double.MAX_VALUE).unique();
        new DoubleSeriesAsserts(s1).expectData(0., -1.1, 1.1, 375.05, Double.MAX_VALUE, 5.1);
    }

    @Test
    public void alreadyUnique() {
        DoubleSeries s1 = Series.ofDouble(0., -1.1, 1.1, 375.05, Double.MAX_VALUE, 5.1).unique();
        new DoubleSeriesAsserts(s1).expectData(0., -1.1, 1.1, 375.05, Double.MAX_VALUE, 5.1);
    }

    @Test
    public void small() {
        DoubleSeries s1 = Series.ofDouble(-1.1).unique();
        new DoubleSeriesAsserts(s1).expectData(-1.1);
    }
}
