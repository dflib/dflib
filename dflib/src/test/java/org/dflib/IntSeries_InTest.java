package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class IntSeries_InTest {

    @Test
    public void in() {
        BooleanSeries s = Series.ofInt(1, 2, 3).in(1, 3);
        new SeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void in_OtherIntegralTypes() {
        BooleanSeries s = Series.ofInt(1, 2, 3, 4, 5).in(1L, (short) 2, (byte) 3, BigInteger.valueOf(4), new BigDecimal("5.00"));
        new SeriesAsserts(s).expectData(true, true, true, true, true);
    }

    @Test
    public void in_FloatingPoint() {
        // integral values match, fractional ones can not
        BooleanSeries s = Series.ofInt(1, 2, 3).in(1.0, 2.5f, 3.0d, Double.NaN);
        new SeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void in_OutOfRange() {
        BooleanSeries s = Series.ofInt(1, 2).in(3_000_000_000L, new BigDecimal("1e20"), 1e19, 2);
        new SeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void in_NonNumeric() {
        BooleanSeries s = Series.ofInt(1, 2).in("1", null, 2);
        new SeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void in_Empty() {
        BooleanSeries s = Series.ofInt(1, 2).in();
        new SeriesAsserts(s).expectData(false, false);
    }

    @Test
    public void notIn() {
        BooleanSeries s = Series.ofInt(1, 2, 3).notIn(1L, 3.0);
        new SeriesAsserts(s).expectData(false, true, false);
    }

    @Test
    public void notIn_Empty() {
        BooleanSeries s = Series.ofInt(1, 2).notIn();
        new SeriesAsserts(s).expectData(true, true);
    }
}
