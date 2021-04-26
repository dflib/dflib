package com.nhl.dflib.exp;

import com.nhl.dflib.*;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;

public class NumericExpTest {

    @Test
    public void testIntAddInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        Series<? extends Number> s = $int("b").add($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(3, 7);
    }

    @Test
    public void testIntAddInt_Primitive() {
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
    public void testDoubleAddDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2.,
                3., 4.5);

        Series<? extends Number> s = $double("b").add($double("a")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.5);
    }

    @Test
    public void testDoubleAddDouble_Primitive() {
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
    public void testIntAddDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2,
                3., 4);

        Series<? extends Number> s = $int("b").add($double("a")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.);
    }

    @Test
    public void testIntAddLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, 2,
                3L, 4);

        Series<? extends Number> s = $int("b").add($long("a")).eval(df);
        new SeriesAsserts(s).expectData(3L, 7L);
    }

    @Test
    public void testDoubleAddInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2,
                3., 4);

        Series<? extends Number> s = $double("a").add($int("b")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.);
    }

    @Test
    public void testIntSubtractInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        Series<? extends Number> s = $int("b").subtract($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(1, 1);
    }

    @Test
    public void testDoubleSubtractDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, 2.,
                3., 4.5);

        Series<? extends Number> s = $double("b").subtract($double("a")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(0.99, 1.5);
    }

    @Test
    public void testLongAddLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, 2000_000_000_001L,
                3L, 4L);

        Series<? extends Number> s = $long("b").add($long("a")).eval(df);
        assertFalse(s instanceof LongSeries);
        new SeriesAsserts(s).expectData(2000_000_000_002L, 7L);
    }

    @Test
    public void testLongAddLong_Primitive() {
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
    public void testIntMultiplyInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                6, 2,
                3, 5);

        Series<? extends Number> s = $int("b").multiply($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(12, 15);
    }

    @Test
    public void testIntDivideDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                2., 3,
                3., 9);

        Series<? extends Number> s = $int("b").divide($double("a")).eval(df);
        new SeriesAsserts(s).expectData(1.5, 3.);
    }

    @Test
    public void testDecimalAddDecimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("1.01"), new BigDecimal("2."),
                new BigDecimal("3."), new BigDecimal("4.5"));

        Series<? extends Number> s = $decimal("b").add($decimal("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("3.01"), new BigDecimal("7.5"));
    }

    @Test
    public void testDecimalDivideInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("35"), 2,
                new BigDecimal("3.3"), 3);

        Series<? extends Number> s = $decimal("a").divide($int("b")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("17.5"), new BigDecimal("1.1"));
    }

    @Test
    public void testDecimalDivideDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("5.0"), 2.5,
                new BigDecimal("3.3"), 3.33);

        Series<? extends Number> s = $decimal("a").divide($double("b")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2"), new BigDecimal("0.99099099099099096984560210653599037467692134485397"));
    }

    @Test
    public void testIntMultiplyDecimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("35.1"), 2,
                new BigDecimal("3.3"), 3);

        Series<? extends Number> s = $int("b").multiply($decimal("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("70.2"), new BigDecimal("9.9"));
    }

    @Test
    public void testDecimalCastAsDecimal() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        Series<? extends Number> s = $decimal("a").castAsDecimal(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void testDoubleCastAsDecimal() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2.0100287,
                4.5);

        Series<? extends Number> s = $double("a").castAsDecimal(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void testIntCastAsDecimal() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2,
                355,
                -3);

        Series<? extends Number> s = $int("a").castAsDecimal(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.00"), new BigDecimal("355.00"), new BigDecimal("-3.00"));
    }

    @Test
    public void testLongCastAsDecimal() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                2L,
                355L,
                -3L);

        Series<? extends Number> s = $long("a").castAsDecimal(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.00"), new BigDecimal("355.00"), new BigDecimal("-3.00"));
    }
}
