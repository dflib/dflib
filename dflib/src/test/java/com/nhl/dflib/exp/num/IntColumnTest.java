package com.nhl.dflib.exp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class IntColumnTest {

    @Test
    public void testGetColumnName() {
        assertEquals("a", $int("a").getColumnName());
        assertEquals("$int(0)", $int(0).getColumnName());
    }

    @Test
    public void testGetColumnName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $int("b").getColumnName(df));
        assertEquals("a", $int(0).getColumnName(df));
    }

    @Test
    public void testAs() {
        NumExp<Integer> e = $int("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void testEval() {
        NumExp<Integer> e = $int("b");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", 2, 3,
                "4", Integer.MAX_VALUE, 6);

        new SeriesAsserts(e.eval(df)).expectData(2, Integer.MAX_VALUE);
    }

    @Test
    public void testCastAsDecimal() {
        DecimalExp e = $int("a").castAsDecimal().scale(2);
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2,
                355,
                -3);

        new SeriesAsserts(e.eval(df))
                .expectData(new BigDecimal("2.00"), new BigDecimal("355.00"), new BigDecimal("-3.00"));
    }

    @Test
    public void testDivide_Double() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                2., 3,
                3., 9);

        Series<? extends Number> s = $int("b").div($double("a")).eval(df);
        new SeriesAsserts(s).expectData(1.5, 3.);
    }

    @Test
    public void testMod() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                2, 3,
                3, 9);

        Series<? extends Number> s = $int("b").mod($int("a")).eval(df);
        new SeriesAsserts(s).expectData(1, 0);
    }

    @Test
    public void testMod_IntVal() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                5,
                0,
                11);

        Series<? extends Number> s = $int("a").mod(5).eval(df);
        new SeriesAsserts(s).expectData(0, 0, 1);
    }

    @Test
    public void testAbs() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                -5,
                0,
                11);

        Series<? extends Number> s = $int("a").abs().eval(df);
        new SeriesAsserts(s).expectData(5, 0, 11);
    }

    @Test
    public void testAdd_Long() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, 2,
                3L, 4);

        Series<? extends Number> s = $int("b").add($long("a")).eval(df);
        new SeriesAsserts(s).expectData(3L, 7L);
    }

    @Test
    public void testSubtract_Int() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        Series<? extends Number> s = $int("b").sub($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(1, 1);
    }

    @Test
    public void testMultiply_Int() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                6, 2,
                3, 5);

        Series<? extends Number> s = $int("b").mul($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(12, 15);
    }

    @Test
    public void testMultiply_Decimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("35.1"), 2,
                new BigDecimal("3.3"), 3);

        Series<? extends Number> s = $int("b").mul($decimal("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("70.2"), new BigDecimal("9.9"));
    }


    @Test
    public void testAdd_Int() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        Series<? extends Number> s = $int("b").add($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(3, 7);
    }

    @Test
    public void testAdd_IntPrimitive() {
        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(IntStream.of(1, 2, 3, 4));

        // sanity check of the test DataFrame
        Series<Integer> a = df.getColumn("a");
        assertTrue(a instanceof IntSeries);

        Series<Integer> b = df.getColumn("b");
        assertTrue(b instanceof IntSeries);

        // run and verify the calculation
        Series<? extends Number> s = $int("b").add($int("a")).eval(df);
        assertTrue(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(3, 7);
    }

    @Test
    public void testAdd_Double() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2,
                3., 4);

        Series<? extends Number> s = $int("b").add($double("a")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.);
    }

    @Test
    public void testLT_Double() {
        Condition c = $int("b").lt($double("a"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, -1,
                3., 4,
                3., 3);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void testGT_Int() {
        Condition c = $int("a").gt($int("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }


    @Test
    public void testGE_Int() {

        Condition c = $int("a").ge($int("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, true, false);
    }

    @Test
    public void testEQ_IntVal() {

        Condition c = $int("a").eq(3);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void testEQ_LongVal() {

        Condition c = $int("a").eq(3L);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        new BooleanSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void testMap_Unary() {

        Exp<String> exp = $int("b").map(s -> s.map(i -> "_" + i));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        new SeriesAsserts(exp.eval(df)).expectData("_2", "_4");
    }

    @Test
    public void testMapVal_Unary() {

        Exp<String> exp = $int("b").mapVal(i -> "_" + i);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4,
                5, null);

        new SeriesAsserts(exp.eval(df)).expectData("_2", "_4", null);
    }


    @Test
    public void testMap_Binary() {

        Exp<Boolean> exp = $int("b").map($int("a"), Series::eq);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 3);

        new SeriesAsserts(exp.eval(df)).expectData(false, true);
    }

    @Test
    public void testMapVal_Binary() {

        Exp<Boolean> exp = $int("b").mapVal($int("a"), Integer::equals);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 3,
                5, null);

        new SeriesAsserts(exp.eval(df)).expectData(false, true, null);
    }

    @Test
    public void testCumSum() {
        NumExp<?> exp = $int("a").cumSum();

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                null,
                2,
                5,
                null,
                11,
                -12);

        new SeriesAsserts(exp.eval(df)).expectData(
                null,
                2L,
                7L,
                null,
                18L,
                6L
        );
    }

    @Test
    public void testCumSum_getColumnName() {
        NumExp<?> exp = $int("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void testSum_getColumnName() {
        NumExp<?> exp = $int("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }
}
