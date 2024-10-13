package org.dflib;

import org.dflib.unit.FloatSeriesAsserts;
import org.junit.jupiter.api.Test;

public class FloatSeries_UniqueTest {

    @Test
    public void test() {
        FloatSeries s1 = Series.ofFloat(0f, -1.1f, -1.1f, 0f, 1.1f, 375.05f, Float.MAX_VALUE, 5.1f, Float.MAX_VALUE).unique();
        new FloatSeriesAsserts(s1).expectData(0f, -1.1f, 1.1f, 375.05f, Float.MAX_VALUE, 5.1f);
    }

    @Test
    public void alreadyUnique() {
        FloatSeries s1 = Series.ofFloat(0f, -1.1f, 1.1f, 375.05f, Float.MAX_VALUE, 5.1f).unique();
        new FloatSeriesAsserts(s1).expectData(0f, -1.1f, 1.1f, 375.05f, Float.MAX_VALUE, 5.1f);
    }

    @Test
    public void small() {
        FloatSeries s1 = Series.ofFloat(-1.1f).unique();
        new FloatSeriesAsserts(s1).expectData(-1.1f);
    }
}
