package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.nhl.dflib.Exp.*;

public class NumExp_CastAsDecimalTest {

    @Test
    public void testDecimal() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        Series<BigDecimal> s = $decimal("a").castAsDecimal().scale(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void testDouble() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2.0100287,
                4.5);

        Series<BigDecimal> s = $double("a").castAsDecimal().scale(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void testInt() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2,
                355,
                -3);

        Series<BigDecimal> s = $int("a").castAsDecimal().scale(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.00"), new BigDecimal("355.00"), new BigDecimal("-3.00"));
    }

    @Test
    public void testLong() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2L,
                355L,
                -3L);

        Series<BigDecimal> s = $long("a").castAsDecimal().scale(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.00"), new BigDecimal("355.00"), new BigDecimal("-3.00"));
    }
}
