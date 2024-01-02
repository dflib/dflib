package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_UniqueTest {

    @Test
    public void test() {
        IntSeries s1 = Series.ofInt(0, -1, -1, 0, 1, 375, Integer.MAX_VALUE, 5, Integer.MAX_VALUE).unique();
        new IntSeriesAsserts(s1).expectData(0, -1, 1, 375, Integer.MAX_VALUE, 5);
    }

    @Test
    public void alreadyUnique() {
        IntSeries s1 = Series.ofInt(0, -1, 1, 375, Integer.MAX_VALUE, 5).unique();
        new IntSeriesAsserts(s1).expectData(0, -1, 1, 375, Integer.MAX_VALUE, 5);
    }

    @Test
    public void small() {
        IntSeries s1 = Series.ofInt(-1).unique();
        new IntSeriesAsserts(s1).expectData(-1);
    }
}
