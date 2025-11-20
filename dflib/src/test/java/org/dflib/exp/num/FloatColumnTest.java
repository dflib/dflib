package org.dflib.exp.num;

import org.dflib.*;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class FloatColumnTest extends BaseExpTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $float("a").getColumnName());
        assertEquals("$float(0)", $float(0).getColumnName());
    }

    @Test
    public void name_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $float("b").toQL(df));
        assertEquals("a", $float(0).toQL(df));
    }

    @Test
    public void chainStaysNumeric() {
        NumExp<?> exp = $float("b").as("x").as("y").sum().as("SUM(x)");
        assertEquals("SUM(x)", exp.getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void as() {
        NumExp<Float> e = $float("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void eval() {
        NumExp<Float> e = $float("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", 2.1f, 3f,
                "4", Float.MAX_VALUE, 6f);

        new SeriesAsserts(e.eval(df)).expectData(2.1f, Float.MAX_VALUE);
    }

    @Test
    public void round() {
        DataFrame df = DataFrame.foldByRow("a").of(
                2.0100287f,
                4.5f,
                0.00005f,
                1.4999999f,
                -0.00005f,
                -2.0100287f);

        Series<? extends Number> s = $float("a").round().eval(df);
        new SeriesAsserts(s).expectData(2, 5, 0, 1, 0, -2);
    }

    @Test
    public void negate() {
        DataFrame df = DataFrame.foldByRow("a").of(
                1.5f, 0f, -0f, -2.25f,
                Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN);

        Series<?> s = $float("a").negate().eval(df);
        new SeriesAsserts(s).expectData(
                -1.5f, -0f, 0f, 2.25f,
                Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN);
    }

    @Test
    public void castAsFloat() {
        NumExp<Float> e = $float("a");
        assertSame(e, e.castAsFloat());
    }

    @Test
    public void castAdDecimal() {
        DecimalExp e = $float("a").castAsDecimal().scale(2);
        DataFrame df = DataFrame.foldByRow("a").of(
                2.0100287f,
                4.5f);

        new SeriesAsserts(e.eval(df)).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void add_Float() {
        NumExp<?> e = $float("b").add($float("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01f, 2f,
                3f, 4.5f);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof FloatSeries);
        new SeriesAsserts(s).expectData(3.01f, 7.5f);
    }

    @Test
    public void add_FloatPrimitive() {

        NumExp<?> e = $float("b").add($float("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").ofFloats(0f, 1.01f, 2f, 3f, 4.5f);

        // sanity check of the test DataFrame
        Series<Float> a = df.getColumn("a");
        assertTrue(a instanceof FloatSeries);

        Series<Float> b = df.getColumn("b");
        assertTrue(b instanceof FloatSeries);

        // run and verify the calculation
        Series<? extends Number> s = e.eval(df);
        assertTrue(s instanceof FloatSeries);
        new SeriesAsserts(s).expectData(3.01f, 7.5f);
    }

    @Test
    public void add_Int() {
        NumExp<?> e = $float("a").add($int("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01f, 2,
                3f, 4);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof FloatSeries);
        new SeriesAsserts(s).expectData(3.01f, 7f);
    }

    @Test
    public void add_Double() {
        NumExp<?> e = $float("b").add($double("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01d, 2f,
                3d, 4.5f);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.5);
    }

    @Test
    public void subtract_Double() {
        NumExp<?> e = $float("b").sub($double("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01d, 2f,
                3d, 4.5f);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(0.99, 1.5);
    }

    @Test
    public void abs() {
        DataFrame df = DataFrame.foldByRow("a").of(
                -5.1f,
                0.0f,
                11.5f);

        Series<? extends Number> s = $float("a").abs().eval(df);
        new SeriesAsserts(s).expectData(5.1f, 0.0f, 11.5f);
    }

    @Test
    public void between_FloatPrimitive() {
        Condition c = $float("a").between($float("b"), $val(5.f));

        DataFrame df = DataFrame.foldByRow("a", "b").ofFloats(
                0.0f,
                0, 1,
                1, 1,
                2, 1,
                5, 2,
                6, 2);

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true, true, false);
    }

    @Test
    public void notBetween_FloatPrimitive() {
        Condition c = $float("a").notBetween($float("b"), $val(5.f));

        DataFrame df = DataFrame.foldByRow("a", "b").ofFloats(
                0.0f,
                0, 1,
                1, 1,
                2, 1,
                5, 2,
                6, 2);

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false, false, true);
    }

    @Test
    public void cumSum() {
        NumExp<?> exp = $float("a").cumSum();

        DataFrame df = DataFrame.foldByRow("a").of(
                null,
                2.0f,
                5.3f,
                null,
                11.1f);

        new SeriesAsserts(exp.eval(df)).expectData(
                null,
                2.0,
                7.300000190734863,
                null,
                18.40000057220459
        );
    }

    @Test
    public void cumSum_getColumnName() {
        NumExp<?> exp = $float("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void sum_getColumnName() {
        NumExp<?> exp = $float("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $float("a"),
                $float("a"),
                $float("b"));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $float("a"),
                $float("a"),
                $float("b"));
    }
}
