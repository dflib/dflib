package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_MulTest {

    @Test
    public void mul_Value() {

        DoubleSeries s = Series.ofDouble(3, 28, 15, -4, 3, 11).mul(10);
        new DoubleSeriesAsserts(s).expectData(30, 280, 150, -40, 30, 110);
    }

}
