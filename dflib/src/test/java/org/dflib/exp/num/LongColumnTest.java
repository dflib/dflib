package org.dflib.exp.num;

import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DecimalExp;
import org.dflib.LongSeries;
import org.dflib.NumExp;
import org.dflib.Series;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.LongStream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class LongColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $long("a").getColumnName());
        assertEquals("$long(0)", $long(0).getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $long("b").getColumnName(df));
        assertEquals("a", $long(0).getColumnName(df));
    }

    @Test
    public void chainStaysNumeric() {
        NumExp<?> exp = $long("b").as("x").as("y").sum().as("SUM(x)");
        assertEquals("SUM(x)", exp.getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void as() {
        NumExp<Long> e = $long("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void eval() {
        NumExp<Long> e = $long("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", 2L, 3L,
                "4", Long.MAX_VALUE, 6L);

        new SeriesAsserts(e.eval(df)).expectData(2L, Long.MAX_VALUE);
    }

    @Test
    public void round() {
        DataFrame df = DataFrame.foldByRow("a").of(
                2L,
                5L,
                0L,
                1L,
                -2L);

        Series<? extends Number> s = $long("a").round().eval(df);
        new SeriesAsserts(s).expectData(2L, 5L, 0L, 1L, -2L);
    }

    @Test
    public void castAsLong() {
        NumExp<Long> e = $long("a");
        assertSame(e, e.castAsLong());
    }

    @Test
    public void castAsDecimal() {

        DecimalExp e = $long("a").castAsDecimal().scale(2);
        DataFrame df = DataFrame.foldByRow("a").of(
                2L,
                355L,
                -3L);

        new SeriesAsserts(e.eval(df)).expectData(new BigDecimal("2.00"), new BigDecimal("355.00"), new BigDecimal("-3.00"));
    }

    @Test
    public void add_Long() {

        NumExp<?> e = $long("b").add($long("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, 2000_000_000_001L,
                3L, 4L);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof LongSeries);
        new SeriesAsserts(s).expectData(2000_000_000_002L, 7L);
    }

    @Test
    public void add_LongPrimitive() {

        NumExp<?> e = $long("b").add($long("a"));

        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(LongStream.of(1L, 2000_000_000_001L, 3L, 4L));

        // sanity check of the test DataFrame
        Series<Long> a = df.getColumn("a");
        assertTrue(a instanceof LongSeries);

        Series<Long> b = df.getColumn("b");
        assertTrue(b instanceof LongSeries);

        // run and verify the calculation
        Series<? extends Number> s = e.eval(df);
        assertTrue(s instanceof LongSeries);
        new SeriesAsserts(s).expectData(2000_000_000_002L, 7L);
    }

    @Test
    public void le_Int() {
        Condition c = $long("a").le($int("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, -1,
                3L, 3,
                3L, 4);

        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void lt_LongPrimitive() {
        Condition c = $long("a").lt($long("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(LongStream.of(2L, 1L, 3L, 4L));
        // sanity check of the test DataFrame
        Series<Long> a = df.getColumn("a");
        assertTrue(a instanceof LongSeries);

        Series<Long> b = df.getColumn("b");
        assertTrue(b instanceof LongSeries);

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(df)).expectData(false, true);
    }

    @Test
    public void between_LongPrimitive() {
        Condition c = $long("a").between($long("b"), $val(5));

        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(LongStream.of(
                0L, 1L,
                1L, 1L,
                2L, 1L,
                5L, 2L,
                6L, 2L));

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true, true, false);
    }

    @Test
    public void eq_IntVal() {

        Condition c = $long("a").eq(3);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, -1,
                3L, 3,
                3L, 4);

        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void ne_IntVal() {

        Condition c = $long("a").ne(3);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, -1,
                3L, 3,
                3L, 4);

        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }


    @Test
    public void nE_Decimal() {

        Condition c = $long("a").ne(new BigDecimal("3"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, -1,
                3L, 3,
                3L, 4);

        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void cumSum() {
        NumExp<?> exp = $long("a").cumSum();

        DataFrame df = DataFrame.foldByRow("a").of(
                null,
                2L,
                5L,
                null,
                110000000L,
                -12L);

        new SeriesAsserts(exp.eval(df)).expectData(
                null,
                2L,
                7L,
                null,
                110000007L,
                109999995L
        );
    }

    @Test
    public void cumSum_getColumnName() {
        NumExp<?> exp = $long("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void sum_getColumnName() {
        NumExp<?> exp = $long("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }
}
