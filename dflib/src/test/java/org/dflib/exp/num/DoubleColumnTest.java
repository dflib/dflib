package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DecimalExp;
import org.dflib.DoubleSeries;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;

public class DoubleColumnTest extends BaseExpTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $double("a").getColumnName());
        assertEquals("double(0)", $double(0).getColumnName());
        assertEquals("a b", $double("a b").getColumnName());
    }

    @Test
    public void name_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $double("b").toQL(df));
        assertEquals("a", $double(0).toQL(df));
    }

    @Test
    public void toQL() {
        assertEquals("a", $double("a").toQL());
        assertEquals("`double(0)`", $double(0).toQL());
        assertEquals("`a b`", $double("a b").toQL());
    }

    @Test
    public void chainStaysNumeric() {
        NumExp<?> exp = $double("b").as("x").as("y").sum().as("SUM(x)");
        assertEquals("SUM(x)", exp.getColumnName(DataFrame.empty()));
    }

    @Test
    public void as() {
        NumExp<Double> e = $double("b");
        assertEquals("b", e.getColumnName(DataFrame.empty()));
        assertEquals("c", e.as("c").getColumnName(DataFrame.empty()));
    }

    @Test
    public void eval() {
        NumExp<Double> e = $double("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", 2.1, 3.,
                "4", Double.MAX_VALUE, 6.);

        new SeriesAsserts(e.eval(df)).expectData(2.1, Double.MAX_VALUE);
    }

    @Test
    public void round() {
        DataFrame df = DataFrame.foldByRow("a").of(
                2.0100287,
                4.5,
                0.00005,
                1.4999999,
                -0.00005,
                -2.0100287);

        Series<? extends Number> s = $double("a").round().eval(df);
        new SeriesAsserts(s).expectData(2L, 5L, 0L, 1L, 0L, -2L);
    }

    @Test
    public void negate() {
        DataFrame df = DataFrame.foldByRow("a").of(
                1.5, 0.0, -0.0, -2.25,
                Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN);

        Series<?> s = $double("a").negate().eval(df);
        new SeriesAsserts(s).expectData(
                -1.5, -0.0, 0.0, 2.25,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN);
    }

    @Test
    public void castAsDouble() {
        NumExp<Double> e = $double("a");
        assertSame(e, e.castAsDouble());
    }

    @Test
    public void castAdDecimal() {
        DecimalExp e = $double("a").castAsDecimal().scale(2);
        DataFrame df = DataFrame.foldByRow("a").of(
                2.0100287,
                4.5);

        new SeriesAsserts(e.eval(df)).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void add_Double() {
        NumExp<?> e = $double("b").add($double("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01, 2.,
                3., 4.5);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.5);
    }

    @Test
    public void add_DoublePrimitive() {

        NumExp<?> e = $double("b").add($double("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(DoubleStream.of(1.01, 2., 3., 4.5));

        // sanity check of the test DataFrame
        Series<Double> a = df.getColumn("a");
        assertTrue(a instanceof DoubleSeries);

        Series<Double> b = df.getColumn("b");
        assertTrue(b instanceof DoubleSeries);

        // run and verify the calculation
        Series<? extends Number> s = e.eval(df);
        assertTrue(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.5);
    }

    @Test
    public void add_Int() {
        NumExp<?> e = $double("a").add($int("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01, 2,
                3., 4);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.);
    }

    @Test
    public void subtract_Double() {
        NumExp<?> e = $double("b").sub($double("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01, 2.,
                3., 4.5);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(0.99, 1.5);
    }

    @Test
    public void abs() {
        DataFrame df = DataFrame.foldByRow("a").of(
                -5.1,
                0.0,
                11.5);

        Series<? extends Number> s = $double("a").abs().eval(df);
        new SeriesAsserts(s).expectData(5.1, 0.0, 11.5);
    }

    @Test
    public void between_DoublePrimitive() {
        Condition c = $double("a").between($double("b"), $val(5.));

        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(DoubleStream.of(
                0, 1,
                1, 1,
                2, 1,
                5, 2,
                6, 2));

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true, true, false);
    }

    @Test
    public void notBetween_DoublePrimitive() {
        Condition c = $double("a").notBetween($double("b"), $val(5.));

        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(DoubleStream.of(
                0, 1,
                1, 1,
                2, 1,
                5, 2,
                6, 2));

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false, false, true);
    }

    @Test
    public void cumSum() {
        NumExp<?> exp = $double("a").cumSum();

        DataFrame df = DataFrame.foldByRow("a").of(
                null,
                2.0,
                5.3,
                null,
                11.1);

        new SeriesAsserts(exp.eval(df)).expectData(
                null,
                2.0,
                7.3,
                null,
                18.4
        );
    }

    @Test
    public void cumSum_getColumnName() {
        NumExp<?> exp = $double("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void sum_getColumnName() {
        NumExp<?> exp = $double("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }

    @Test
    public void eq_Decimal() {

        Condition c = $double("a").eq(new BigDecimal("3"));

        DataFrame df = DataFrame.foldByRow("a").of(
                1.,
                3.,
                3.1);

        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, false);
    }

    @Test
    public void ne_Decimal() {

        Condition c = $double("a").ne(new BigDecimal("3"));

        DataFrame df = DataFrame.foldByRow("a").of(
                1.,
                3.,
                3.1);

        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, true);
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $double("a"),
                $double("a"),
                $double("b"));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $double("a"),
                $double("a"),
                $double("b"));
    }
}
