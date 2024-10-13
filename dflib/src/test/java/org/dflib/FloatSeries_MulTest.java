package org.dflib;

import org.dflib.unit.FloatSeriesAsserts;
import org.junit.jupiter.api.Test;

public class FloatSeries_MulTest {

    @Test
    public void mul_Value() {

        FloatSeries s = Series.ofFloat(3, 28, 15, -4, 3, 11).mul(10);
        new FloatSeriesAsserts(s).expectData(30, 280, 150, -40, 30, 110);
    }

}
