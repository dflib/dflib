package com.nhl.dflib.exp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.LongStream;

import static com.nhl.dflib.Exp.$int;
import static com.nhl.dflib.Exp.$long;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class LongColumnTest {

    @Test
    public void testGetColumnName() {
        assertEquals("a", $long("a").getColumnName());
        assertEquals("$long(0)", $long(0).getColumnName());
    }

    @Test
    public void testGetColumnName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $long("b").getColumnName(df));
        assertEquals("a", $long(0).getColumnName(df));
    }

    @Test
    public void testAs() {
        NumExp<Long> e = $long("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void testEval() {
        NumExp<Long> e = $long("b");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", 2L, 3L,
                "4", Long.MAX_VALUE, 6L);

        new SeriesAsserts(e.eval(df)).expectData(2L, Long.MAX_VALUE);
    }

    @Test
    public void testCastAsDecimal() {

        DecimalExp e = $long("a").castAsDecimal().scale(2);
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2L,
                355L,
                -3L);

        new SeriesAsserts(e.eval(df)).expectData(new BigDecimal("2.00"), new BigDecimal("355.00"), new BigDecimal("-3.00"));
    }

    @Test
    public void testAdd_Long() {

        NumExp<?> e = $long("b").add($long("a"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, 2000_000_000_001L,
                3L, 4L);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof LongSeries);
        new SeriesAsserts(s).expectData(2000_000_000_002L, 7L);
    }

    @Test
    public void testAdd_LongPrimitive() {

        NumExp<?> e = $long("b").add($long("a"));

        DataFrame df = DataFrame.newFrame("a", "b")
                .foldLongStreamByRow(LongStream.of(1L, 2000_000_000_001L, 3L, 4L));

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
    public void testLE_Int() {
        Condition c = $long("a").le($int("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void testLT_LongPrimitive() {
        Condition c = $long("a").lt($long("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldLongStreamByRow(LongStream.of(2L, 1L, 3L, 4L));
        // sanity check of the test DataFrame
        Series<Long> a = df.getColumn("a");
        assertTrue(a instanceof LongSeries);

        Series<Long> b = df.getColumn("b");
        assertTrue(b instanceof LongSeries);

        // run and verify the calculation
        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true);
    }

    @Test
    public void testEQ_IntVal() {

        Condition c = $long("a").eq(3);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void testNE_IntVal() {

        Condition c = $long("a").ne(3);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }


    @Test
    public void testNE_Decimal() {

        Condition c = $long("a").ne(new BigDecimal("3"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void testCumSum() {
        NumExp<?> exp = $long("a").cumSum();

        DataFrame df = DataFrame.newFrame("a").foldByRow(
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
    public void testCumSum_getColumnName() {
        NumExp<?> exp = $long("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void testSum_getColumnName() {
        NumExp<?> exp = $long("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }
}
