package com.nhl.dflib.exp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

import static com.nhl.dflib.Exp.$double;
import static com.nhl.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class DoubleColumnTest {

    @Test
    public void testName() {
        assertEquals("a", $double("a").getName());
        assertEquals("$double(0)", $double(0).getName());
    }

    @Test
    public void testName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $double("b").getName(df));
        assertEquals("a", $double(0).getName(df));
    }

    @Test
    public void testAs() {
        NumExp<Double> e = $double("b");
        assertEquals("b", e.getName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getName(mock(DataFrame.class)));
    }

    @Test
    public void testEval() {
        NumExp<Double> e = $double("b");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", 2.1, 3.,
                "4", Double.MAX_VALUE, 6.);

        new SeriesAsserts(e.eval(df)).expectData(2.1, Double.MAX_VALUE);
    }

    @Test
    public void testCastAdDecimal() {
        DecimalExp e = $double("a").castAsDecimal().scale(2);
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2.0100287,
                4.5);

        new SeriesAsserts(e.eval(df)).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void testAdd_Double() {
        NumExp<?> e = $double("b").add($double("a"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2.,
                3., 4.5);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.5);
    }

    @Test
    public void testAdd_DoublePrimitive() {

        NumExp<?> e = $double("b").add($double("a"));

        DataFrame df = DataFrame.newFrame("a", "b").foldDoubleStreamByRow(DoubleStream.of(1.01, 2., 3., 4.5));

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
    public void testAdd_Int() {
        NumExp<?> e = $double("a").add($int("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2,
                3., 4);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.);
    }

    @Test
    public void testSubtract_Double() {
        NumExp<?> e = $double("b").sub($double("a"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2.,
                3., 4.5);

        Series<? extends Number> s = e.eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(0.99, 1.5);
    }
}
