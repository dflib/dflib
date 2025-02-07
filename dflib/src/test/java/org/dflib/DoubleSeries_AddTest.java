package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_AddTest {

    @Test
    public void add_Series() {

        // using a longer series to test optional vectorization extensions
        DoubleSeries s0 = Series.ofDouble(1, 2, 3, 4, 5, 6.56);
        DoubleSeries s = Series.ofDouble(3, 28, 15, -4, 3, 11).add(s0);
        new DoubleSeriesAsserts(s).expectData(4., 30., 18., 0., 8., 17.56);
    }

    @Test
    public void add_Value() {

        DoubleSeries s = Series.ofDouble(3, 28, 15, -4, 3, 11).add(10.5);
        new DoubleSeriesAsserts(s).expectData(13.5, 38.5, 25.5, 6.5, 13.5, 21.5);
    }
}
