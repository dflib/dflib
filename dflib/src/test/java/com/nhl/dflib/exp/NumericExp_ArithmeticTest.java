package com.nhl.dflib.exp;

import com.nhl.dflib.*;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumericExp_ArithmeticTest {

    @Test
    public void testAdd_LongLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, 2000_000_000_001L,
                3L, 4L);

        Series<? extends Number> s = $long("b").add($long("a")).eval(df);
        assertFalse(s instanceof LongSeries);
        new SeriesAsserts(s).expectData(2000_000_000_002L, 7L);
    }

    @Test
    public void testAdd_LongLong_Primitive() {
        DataFrame df = DataFrame.newFrame("a", "b").foldLongStreamByRow(LongStream.of(1L, 2000_000_000_001L, 3L, 4L));

        // sanity check of the test DataFrame
        Series<Long> a = df.getColumn("a");
        assertTrue(a instanceof LongSeries);

        Series<Long> b = df.getColumn("b");
        assertTrue(b instanceof LongSeries);

        // run and verify the calculation
        Series<? extends Number> s = $long("b").add($long("a")).eval(df);
        assertTrue(s instanceof LongSeries);
        new SeriesAsserts(s).expectData(2000_000_000_002L, 7L);
    }

    @Test
    public void testAdd_DecimalDecimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("1.01"), new BigDecimal("2."),
                new BigDecimal("3."), new BigDecimal("4.5"));

        Series<? extends Number> s = $decimal("b").add($decimal("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("3.01"), new BigDecimal("7.5"));
    }

    @Test
    public void testAdd_IntInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        Series<? extends Number> s = $int("b").add($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(3, 7);
    }

    @Test
    public void testAdd_IntInt_Primitive() {
        DataFrame df = DataFrame.newFrame("a", "b").foldIntStreamByRow(IntStream.of(1, 2, 3, 4));

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
    public void testAdd_DoubleDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2.,
                3., 4.5);

        Series<? extends Number> s = $double("b").add($double("a")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.5);
    }

    @Test
    public void testAdd_DoubleDouble_Primitive() {
        DataFrame df = DataFrame.newFrame("a", "b").foldDoubleStreamByRow(DoubleStream.of(1.01, 2., 3., 4.5));

        // sanity check of the test DataFrame
        Series<Double> a = df.getColumn("a");
        assertTrue(a instanceof DoubleSeries);

        Series<Double> b = df.getColumn("b");
        assertTrue(b instanceof DoubleSeries);

        // run and verify the calculation
        Series<? extends Number> s = $double("b").add($double("a")).eval(df);
        assertTrue(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.5);
    }

    @Test
    public void testAdd_IntDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2,
                3., 4);

        Series<? extends Number> s = $int("b").add($double("a")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.);
    }

    @Test
    public void testAdd_IntLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, 2,
                3L, 4);

        Series<? extends Number> s = $int("b").add($long("a")).eval(df);
        new SeriesAsserts(s).expectData(3L, 7L);
    }

    @Test
    public void testAdd_DoubleInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2,
                3., 4);

        Series<? extends Number> s = $double("a").add($int("b")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.);
    }

    @Test
    public void testSubtract_IntInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        Series<? extends Number> s = $int("b").sub($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(1, 1);
    }

    @Test
    public void testSubtract_DoubleDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2.,
                3., 4.5);

        Series<? extends Number> s = $double("b").sub($double("a")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(0.99, 1.5);
    }

    @Test
    public void testMultiply_IntInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                6, 2,
                3, 5);

        Series<? extends Number> s = $int("b").mul($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(12, 15);
    }

    @Test
    public void testMultiply_IntDecimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("35.1"), 2,
                new BigDecimal("3.3"), 3);

        Series<? extends Number> s = $int("b").mul($decimal("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("70.2"), new BigDecimal("9.9"));
    }

    @Test
    public void testDivide_DecimalInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("35"), 2,
                new BigDecimal("3.3"), 3);

        Series<? extends Number> s = $decimal("a").div($int("b")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("17.5"), new BigDecimal("1.1"));
    }

    @Test
    public void testDivide_DecimalDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("5.0"), 2.5,
                new BigDecimal("3.3"), 3.33);

        Series<? extends Number> s = $decimal("a").div($double("b")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2"), new BigDecimal("0.99099099099099096984560210653599037467692134485397"));
    }

    @Test
    public void testDivide_IntDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                2., 3,
                3., 9);

        Series<? extends Number> s = $int("b").div($double("a")).eval(df);
        new SeriesAsserts(s).expectData(1.5, 3.);
    }

    @Test
    public void testMod_IntInt() {
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



}
