package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_UniqueTest {

    @Test
    public void test() {
        IntSeries s1 = Series.ofInt(0, -1, -1, 0, 1, 375, Integer.MAX_VALUE, 5, Integer.MAX_VALUE).uniqueInt();
        new IntSeriesAsserts(s1).expectData(0, -1, 1, 375, Integer.MAX_VALUE, 5);
    }

    @Test
    public void testAlreadyUnique() {
        IntSeries s1 = Series.ofInt(0, -1, 1, 375, Integer.MAX_VALUE, 5).uniqueInt();
        new IntSeriesAsserts(s1).expectData(0, -1, 1, 375, Integer.MAX_VALUE, 5);
    }

    @Test
    public void testSmall() {
        IntSeries s1 = Series.ofInt(-1).uniqueInt();
        new IntSeriesAsserts(s1).expectData(-1);
    }
}
