package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class LongSeries_InTest {

    @Test
    public void in() {
        BooleanSeries s = Series.ofLong(1L, 2L, 3L).in(1L, 3L);
        new SeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void in_OtherIntegralTypes() {
        BooleanSeries s = Series.ofLong(1L, 2L, 3L, 4L, 5L).in(1, (short) 2, (byte) 3, BigInteger.valueOf(4), new BigDecimal("5.00"));
        new SeriesAsserts(s).expectData(true, true, true, true, true);
    }

    @Test
    public void in_FloatingPoint() {
        BooleanSeries s = Series.ofLong(1L, 2L, 3L).in(1.0, 2.5f, 3.0d);
        new SeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void in_OutOfRange() {
        BooleanSeries s = Series.ofLong(1L, Long.MAX_VALUE).in(BigInteger.ONE.shiftLeft(64), new BigDecimal("1e20"), 1e19, 1);
        new SeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void in_NonNumeric() {
        BooleanSeries s = Series.ofLong(1L, 2L).in("1", null, 2);
        new SeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void in_Empty() {
        BooleanSeries s = Series.ofLong(1L, 2L).in();
        new SeriesAsserts(s).expectData(false, false);
    }

    @Test
    public void notIn() {
        BooleanSeries s = Series.ofLong(1L, 2L, 3L).notIn(1, 3.0);
        new SeriesAsserts(s).expectData(false, true, false);
    }

    @Test
    public void notIn_Empty() {
        BooleanSeries s = Series.ofLong(1L, 2L).notIn();
        new SeriesAsserts(s).expectData(true, true);
    }
}
