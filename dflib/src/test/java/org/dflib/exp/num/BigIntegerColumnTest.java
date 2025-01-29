package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.BigIntegerExp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.dflib.Exp.$bigInteger;
import static org.dflib.Exp.$double;
import static org.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

public class BigIntegerColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $bigInteger("a").getColumnName());
        assertEquals("$bigInteger(0)", $bigInteger(0).getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $bigInteger("b").getColumnName(df));
        assertEquals("a", $bigInteger(0).getColumnName(df));
    }

    @Test
    public void eval() {
        BigIntegerExp exp = $bigInteger("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", new BigInteger("2"), new BigInteger("3"),
                "4", new BigInteger("5"), new BigInteger("6"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigInteger("2"), new BigInteger("5"));
    }

    @Test
    public void chainStaysBigInteger() {
        BigIntegerExp exp = $bigInteger("b").as("x").as("y").sum().as("SUM(x)");
        assertEquals("SUM(x)", exp.getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void as() {
        BigIntegerExp exp = $bigInteger("b");
        assertEquals("b", exp.getColumnName(mock(DataFrame.class)));
        assertEquals("c", exp.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void round() {
        DataFrame df = DataFrame.foldByRow("a").of(new BigInteger("4"), new BigInteger("-2"));

        Series<BigInteger> s = $bigInteger("a").round().eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("4"), new BigInteger("-2"));
    }

    @Test
    public void castAsBigInteger() {
        BigIntegerExp e = $bigInteger("a");
        assertSame(e, e.castAsBigInteger());
    }

    @Test
    public void castAsBigInteger_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("20100287"),
                new BigInteger("45"));

        Series<BigInteger> s = $bigInteger("a").castAsBigInteger().eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("20100287"), new BigInteger("45"));
    }

    @Test
    public void castAsStr() {
        StrExp str = $bigInteger(0).castAsStr();
        Series<BigInteger> s = Series.of(new BigInteger("501"), null);
        new SeriesAsserts(str.eval(s)).expectData("501", null);
    }

    @Test
    public void add_IntPrimitive() {

        BigIntegerExp exp = $bigInteger("a").add(1);

        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("2"),
                new BigInteger("4"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigInteger("3"), new BigInteger("5"));
    }

    @Test
    public void cumSum() {
        BigIntegerExp exp = $bigInteger("a").cumSum();

        DataFrame df = DataFrame.foldByRow("a").of(
                null,
                new BigInteger("2"),
                new BigInteger("4"),
                null,
                new BigInteger("11"),
                new BigInteger("-12"));

        new SeriesAsserts(exp.eval(df)).expectData(
                null,
                new BigInteger("2"),
                new BigInteger("6"),
                null,
                new BigInteger("17"),
                new BigInteger("5")
        );
    }

    @Test
    public void sum() {
        BigIntegerExp exp = $bigInteger("a").sum();

        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("2"),
                new BigInteger("4"));

        assertEquals(new BigInteger("6"), exp.reduce(df));
    }

    @Test
    public void sum_Nulls() {
        BigIntegerExp exp = $bigInteger("a").sum();

        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("2"),
                null,
                new BigInteger("4"));

        assertEquals(new BigInteger("6"), exp.reduce(df));
    }

    @Test
    public void median_Zero() {
        NumExp<?> exp = $bigInteger(0).median();
        Series<BigInteger> s = Series.of();
        assertNull(exp.reduce(s));
    }

    @Test
    public void median_One() {
        NumExp<?> exp = $bigInteger(0).median();
        Series<BigInteger> s = Series.of(new BigInteger("100"));
        assertEquals(new BigDecimal("100"), exp.reduce(s));
    }

    @Test
    public void median_Odd() {
        NumExp<?> exp = $bigInteger(0).median();

        Series<BigInteger> s = Series.of(new BigInteger("100"), new BigInteger("50"), new BigInteger("0"));

        assertEquals(new BigDecimal("50"), exp.reduce(s));
    }

    @Test
    public void median_Even() {
        NumExp<?> exp = $bigInteger(0).median();

        Series<BigInteger> s = Series.of(
                new BigInteger("100"), new BigInteger("55"), new BigInteger("0"), new BigInteger("5"));

        assertEquals(new BigDecimal("30.0"), exp.reduce(s));
    }

    @Test
    public void median_Nulls() {
        NumExp<?> exp = $bigInteger(0).median();

        Series<BigInteger> s = Series.of(
                new BigInteger("100"), null, new BigInteger("50"), new BigInteger("0"));

        assertEquals(new BigDecimal("50"), exp.reduce(s));
    }

    @Test
    public void quantile_Nulls() {
        NumExp<?> exp = $bigInteger(0).quantile(0.75);

        Series<BigInteger> s = Series.of(
                new BigInteger("100"),
                null, new BigInteger("55"),
                new BigInteger("0"),
                new BigInteger("-220"),
                new BigInteger("35"),
                new BigInteger("8"));

        assertEquals(new BigDecimal("50.00"), exp.reduce(s));
    }

    @Test
    public void add_BigInteger() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigInteger("101"), new BigInteger("2"),
                new BigInteger("3"), new BigInteger("45"));

        Series<? extends Number> s = $bigInteger("b").add($bigInteger("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("103"), new BigInteger("48"));
    }

    @Test
    public void divide_Int() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigInteger("35"), 2,
                new BigInteger("33"), 3);

        Series<? extends Number> s = $bigInteger("a").div($int("b")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("17.5"), new BigDecimal("11"));
    }

    @Test
    public void divide_Double() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigInteger("5"), 2.5,
                new BigInteger("3"), 0.25);

        Series<? extends Number> s = $bigInteger("a").div($double("b")).eval(df);
        new SeriesAsserts(s).expectData(2.0, 12.00);
    }

    @Test
    public void gT_BigInteger() {
        Condition c = $bigInteger("a").gt($bigInteger("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigInteger("11"), new BigInteger("10"),
                new BigInteger("3"), new BigInteger("3"),
                new BigInteger("11"), new BigInteger("12"));

        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void abs() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("115"));

        Series<? extends Number> s = $bigInteger("a").abs().eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("51"), BigInteger.ZERO, new BigInteger("115"));
    }

    @Test
    public void ne() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("115"));

        BooleanSeries s = $bigInteger("a").ne(new BigInteger("115")).eval(df);
        new BoolSeriesAsserts(s).expectData(true, true, false);
    }

    @Test
    public void eq() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("115"));

        BooleanSeries s = $bigInteger("a").eq(new BigInteger("115")).eval(df);
        new BoolSeriesAsserts(s).expectData(false, false, true);
    }

    @Test
    public void eq_Zero() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("0"),
                new BigInteger("115"));

        BooleanSeries s = $bigInteger("a").eq(BigInteger.ZERO).eval(df);
        new BoolSeriesAsserts(s).expectData(false, true, true, false);
    }

    @Test
    public void ne_NonNumber() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("115"));

        BooleanSeries s = $bigInteger("a").ne("115").eval(df);
        new BoolSeriesAsserts(s).expectData(true, true, true);
    }

    @Test
    public void cumSum_getColumnName() {
        NumExp<?> exp = $bigInteger("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void sum_getColumnName() {
        NumExp<?> exp = $bigInteger("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }

    @Test
    public void castAsBool() {
        Condition c = $bigInteger(0).castAsBool();

        Series<BigInteger> s = Series.of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                null,
                new BigInteger("115"));
        new BoolSeriesAsserts(c.eval(s)).expectData(true, false, false, true);
    }

    @Test
    public void mapBoolVal() {
        Condition c = $bigInteger(0).mapBoolVal(d -> d.longValue() > 0);

        Series<BigInteger> s = Series.of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                null,
                new BigInteger("115"));
        new BoolSeriesAsserts(c.eval(s)).expectData(false, false, false, true);
    }

    @Test
    public void mapBool() {
        Condition c = $bigInteger(0).mapBool(Series::isNotNull);

        Series<BigInteger> s = Series.of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                null,
                new BigInteger("115"));
        new BoolSeriesAsserts(c.eval(s)).expectData(true, true, false, true);
    }

    @Test
    public void between() {
        Condition c = $bigInteger("a").between(new BigInteger("1"), new BigInteger("3"));

        Series<BigInteger> s = Series.of(
                new BigInteger("0"),
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"));

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(s)).expectData(false, true, true, true, false);
    }
}
