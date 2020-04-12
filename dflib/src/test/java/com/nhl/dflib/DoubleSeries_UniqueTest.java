package com.nhl.dflib;

import com.nhl.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_UniqueTest {

    @Test
    public void test() {
        DoubleSeries s1 = DoubleSeries.forDoubles(0., -1.1, -1.1, 0., 1.1, 375.05, Double.MAX_VALUE, 5.1, Double.MAX_VALUE).uniqueDouble();
        new DoubleSeriesAsserts(s1).expectData(0., -1.1, 1.1, 375.05, Double.MAX_VALUE, 5.1);
    }

    @Test
    public void testAlreadyUnique() {
        DoubleSeries s1 = DoubleSeries.forDoubles(0., -1.1, 1.1, 375.05, Double.MAX_VALUE, 5.1).uniqueDouble();
        new DoubleSeriesAsserts(s1).expectData(0., -1.1, 1.1, 375.05, Double.MAX_VALUE, 5.1);
    }

    @Test
    public void testSmall() {
        DoubleSeries s1 = DoubleSeries.forDoubles(-1.1).uniqueDouble();
        new DoubleSeriesAsserts(s1).expectData(-1.1);
    }
}
