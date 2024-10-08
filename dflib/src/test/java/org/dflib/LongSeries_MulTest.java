package org.dflib;

import org.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.api.Test;


public class LongSeries_MulTest {

	@Test
    public void mul_Value() {

        LongSeries s = Series.ofLong(3, 28, 15, -4, 3, 11).mul(10L);
        new LongSeriesAsserts(s).expectData(30L, 280L, 150L, -40L, 30L, 110L);
    }

}
