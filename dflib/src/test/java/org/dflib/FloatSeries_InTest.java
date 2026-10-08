package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FloatSeries_InTest {

    @Test
    public void in() {
        BooleanSeries s = Series.ofFloat(1.5f, 2.5f, 3.5f).in(1.5f, 3.5f);
        new SeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void in_OtherNumericTypes() {
        BooleanSeries s = Series.ofFloat(1f, 2.5f, 3f, 4f, 0.1f).in(1, 2.5, 3L, BigInteger.valueOf(4), new BigDecimal("0.1"));
        new SeriesAsserts(s).expectData(true, true, true, true, true);
    }

    @Test
    public void in_NonNumeric() {
        BooleanSeries s = Series.ofFloat(1f, 2f).in("1", null, 2);
        new SeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void in_Empty() {
        BooleanSeries s = Series.ofFloat(1f, 2f).in();
        new SeriesAsserts(s).expectData(false, false);
    }

    @Test
    public void notIn() {
        BooleanSeries s = Series.ofFloat(1f, 2f, 3f).notIn(1, 3.0);
        new SeriesAsserts(s).expectData(false, true, false);
    }

    @Test
    public void notIn_Empty() {
        BooleanSeries s = Series.ofFloat(1f, 2f).notIn();
        new SeriesAsserts(s).expectData(true, true);
    }
}
