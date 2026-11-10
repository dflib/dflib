package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.AtomicInteger;

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
        // integral values match, fractional ones can not
        BooleanSeries s = Series.ofLong(1L, 2L, 3L).in(1.0, 2.5f, 3.0, Double.NaN);
        new SeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void in_OutOfRange() {
        BooleanSeries s = Series.ofLong(1L, Long.MAX_VALUE).in(BigInteger.ONE.shiftLeft(64), new BigDecimal("1e20"), 1e19, 1);
        new SeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void in_RangeBoundaries() {
        // 2^63 is a double, but not a long, while -2^63 is both
        BooleanSeries s = Series.ofLong(Long.MIN_VALUE, Long.MAX_VALUE, 0L).in(
                -0x1p63,
                0x1p63,
                BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE),
                BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE),
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY);
        new SeriesAsserts(s).expectData(true, false, false);
    }

    @Test
    public void in_RangeBoundaries_BigInteger() {
        BooleanSeries s = Series.ofLong(Long.MIN_VALUE, Long.MAX_VALUE, 0L).in(
                BigInteger.valueOf(Long.MIN_VALUE),
                BigInteger.valueOf(Long.MAX_VALUE));
        new SeriesAsserts(s).expectData(true, true, false);
    }

    @Test
    public void in_OtherNumberSubclasses() {
        DoubleAdder nan = new DoubleAdder();
        nan.add(Double.NaN);

        BooleanSeries s = Series.ofLong(1L, 2L).in(new AtomicInteger(2), nan);
        new SeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void in_NullArray() {
        BooleanSeries s = Series.ofLong(1L, 2L).in((Object[]) null);
        new SeriesAsserts(s).expectData(false, false);
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
    public void notIn_NonNumeric() {
        BooleanSeries s = Series.ofLong(1L, 2L).notIn("1", null);
        new SeriesAsserts(s).expectData(true, true);
    }

    @Test
    public void notIn_Empty() {
        BooleanSeries s = Series.ofLong(1L, 2L).notIn();
        new SeriesAsserts(s).expectData(true, true);
    }
}
