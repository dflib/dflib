package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DoubleSeries_InTest {

    @Test
    public void in() {
        BooleanSeries s = Series.ofDouble(1.5, 2.5, 3.5).in(1.5, 3.5);
        new SeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void in_OtherNumericTypes() {
        BooleanSeries s = Series.ofDouble(1., 2.5, 3., 4., 0.1).in(1, 2.5f, 3L, BigInteger.valueOf(4), new BigDecimal("0.1"));
        new SeriesAsserts(s).expectData(true, true, true, true, true);
    }

    @Test
    public void in_NonNumeric() {
        BooleanSeries s = Series.ofDouble(1., 2.).in("1", null, 2);
        new SeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void in_Empty() {
        BooleanSeries s = Series.ofDouble(1., 2.).in();
        new SeriesAsserts(s).expectData(false, false);
    }

    @Test
    public void notIn() {
        BooleanSeries s = Series.ofDouble(1., 2., 3.).notIn(1, 3.0f);
        new SeriesAsserts(s).expectData(false, true, false);
    }

    @Test
    public void notIn_Empty() {
        BooleanSeries s = Series.ofDouble(1., 2.).notIn();
        new SeriesAsserts(s).expectData(true, true);
    }
}
