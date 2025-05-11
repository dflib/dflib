package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_AddTest {


    @Test
    public void add_Series() {
        IntSeries s0 = Series.ofInt(1, 2, 3, 4, 5, 6);
        IntSeries s = Series.ofInt(3, 28, 15, -4, 3, 11).add(s0);
        new IntSeriesAsserts(s).expectData(4, 30, 18, 0, 8, 17);
    }

	@Test
    public void add_Value() {
        IntSeries s = Series.ofInt(3, 28, 15, -4, 3, 11).add(10);
        new IntSeriesAsserts(s).expectData(13, 38, 25, 6, 13, 21);
    }
}
