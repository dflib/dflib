package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_MulTest {

    @Test
    public void mul() {
        IntSeries s0 = Series.ofInt(1, 2, 3, 4, 5, 6);
        IntSeries s = Series.ofInt(3, 28, 15, -4, 3, 11).mul(s0);
        new IntSeriesAsserts(s).expectData(3, 56, 45, -16, 15, 66);
    }

	@Test
    public void mul_Value() {
        IntSeries s = Series.ofInt(3, 28, 15, -4, 3, 11).mul(2);
        new IntSeriesAsserts(s).expectData(6, 56, 30, -8, 6, 22);
    }

}