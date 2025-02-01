package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.DecimalExp;
import org.dflib.DoubleSeries;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.ExpBaseTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class DoubleColumnTest extends ExpBaseTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $double("a").getColumnName());
        assertEquals("$double(0)", $double(0).getColumnName());
    }

    @Test
    public void name_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $double("b").toQL(df));
        assertEquals("a", $double(0).toQL(df));
    }

    @Test
    public void chainStaysNumeric() {
        NumExp<?> exp = $double("b").as("x").as("y").sum().as("SUM(x)");
        assertEquals("SUM(x)", exp.getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void as() {
        NumExp<Double> e = $double("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
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
    public void equalsHashCode() {
        NumExp<?> e1 = $double("a");
        NumExp<?> e2 = $double("a");
        NumExp<?> e3 = $double("a");
        NumExp<?> different = $double("b");

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different);
    }
}
