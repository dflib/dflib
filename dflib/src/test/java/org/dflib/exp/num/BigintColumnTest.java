package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BigintColumnTest extends BaseExpTest {

    @Test
    public void getColumnName() {
        assertEquals("a", Exp.$bigint("a").getColumnName());
        assertEquals("bigint(0)", $bigint(0).getColumnName());
        assertEquals("a b", $bigint("a b").getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", Exp.$bigint("b").getColumnName(df));
        assertEquals("a", $bigint(0).getColumnName(df));
    }

    @Test
    public void toQL() {
        assertEquals("a", $bigint("a").toQL());
        assertEquals("`bigint(0)`", $bigint(0).toQL());
        assertEquals("`a b`", $bigint("a b").toQL());
    }

    @Test
    public void eval() {
        NumExp<BigInteger> exp = Exp.$bigint("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", new BigInteger("2"), new BigInteger("3"),
                "4", new BigInteger("5"), new BigInteger("6"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigInteger("2"), new BigInteger("5"));
    }

    @Test
    public void chainStaysNumeric() {
        NumExp<?> exp = $bigint("b").as("x").as("y").sum().as("SUM(x)");
        assertEquals("SUM(x)", exp.getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void as() {
        NumExp<BigInteger> exp = $bigint("b");
        assertEquals("b", exp.getColumnName(mock(DataFrame.class)));
        assertEquals("c", exp.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void round() {
        DataFrame df = DataFrame.foldByRow("a").of(new BigInteger("4"), new BigInteger("-2"));

        Series<?> s = $bigint("a").round().eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("4"), new BigInteger("-2"));
    }

    @Test
    public void castAsBigInteger() {
        NumExp<BigInteger> e = $bigint("a");
        assertSame(e, e.castAsBigint());
    }

    @Test
    public void castAsBigInteger_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("20100287"),
                new BigInteger("45"));

        Series<BigInteger> s = $bigint("a").castAsBigint().eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("20100287"), new BigInteger("45"));
    }

    @Test
    public void castAsStr() {
        StrExp str = $bigint(0).castAsStr();
        Series<BigInteger> s = Series.of(new BigInteger("501"), null);
        new SeriesAsserts(str.eval(s)).expectData("501", null);
    }

    @Test
    public void add_IntPrimitive() {

        NumExp<?> exp = $bigint("a").add(1);

        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("2"),
                new BigInteger("4"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigInteger("3"), new BigInteger("5"));
    }

    @Test
    public void cumSum() {
        NumExp<?> exp = $bigint("a").cumSum();

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
        NumExp<?> exp = $bigint("a").sum();

        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("2"),
                new BigInteger("4"));

        assertEquals(new BigInteger("6"), exp.reduce(df));
    }

    @Test
    public void sum_Nulls() {
        NumExp<?> exp = $bigint("a").sum();

        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("2"),
                null,
                new BigInteger("4"));

        assertEquals(new BigInteger("6"), exp.reduce(df));
    }

    @Test
    public void median_Zero() {
        NumExp<?> exp = $bigint(0).median();
        Series<BigInteger> s = Series.of();
        assertNull(exp.reduce(s));
    }

    @Test
    public void median_One() {
        NumExp<?> exp = $bigint(0).median();
        Series<BigInteger> s = Series.of(new BigInteger("100"));
        assertEquals(new BigDecimal("100"), exp.reduce(s));
    }

    @Test
    public void median_Odd() {
        NumExp<?> exp = $bigint(0).median();

        Series<BigInteger> s = Series.of(new BigInteger("100"), new BigInteger("50"), new BigInteger("0"));

        assertEquals(new BigDecimal("50"), exp.reduce(s));
    }

    @Test
    public void median_Even() {
        NumExp<?> exp = $bigint(0).median();

        Series<BigInteger> s = Series.of(
                new BigInteger("100"), new BigInteger("55"), new BigInteger("0"), new BigInteger("5"));

        assertEquals(new BigDecimal("30.0"), exp.reduce(s));
    }

    @Test
    public void median_Nulls() {
        NumExp<?> exp = $bigint(0).median();

        Series<BigInteger> s = Series.of(
                new BigInteger("100"), null, new BigInteger("50"), new BigInteger("0"));

        assertEquals(new BigDecimal("50"), exp.reduce(s));
    }

    @Test
    public void quantile_Nulls() {
        NumExp<?> exp = $bigint(0).quantile(0.75);

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
    public void add_Bigint() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigInteger("101"), new BigInteger("2"),
                new BigInteger("3"), new BigInteger("45"));

        Series<? extends Number> s = $bigint("b").add($bigint("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("103"), new BigInteger("48"));
    }

    @Test
    public void add_Decimal() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigInteger("101"), new BigDecimal("2.001"),
                new BigInteger("3"), new BigDecimal("45.3"));

        Series<? extends Number> s2 = $bigint("a").add($decimal("b")).eval(df);
        new SeriesAsserts(s2).expectData(new BigDecimal("103.001"), new BigDecimal("48.3"));
    }

    @Test
    public void divide_Int() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigInteger("35"), 2,
                new BigInteger("33"), 3);

        Series<? extends Number> s = $bigint("a").div($int("b")).eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("17"), new BigInteger("11"));
    }

    @Test
    public void divide_Double() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigInteger("5"), 2.5,
                new BigInteger("3"), 0.25);

        Series<? extends Number> s = $bigint("a").div($double("b")).eval(df);
        new SeriesAsserts(s).expectData(2.0, 12.00);
    }

    @Test
    public void gt_BigInteger() {
        Condition c = $bigint("a").gt($bigint("b"));

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

        Series<? extends Number> s = $bigint("a").abs().eval(df);
        new SeriesAsserts(s).expectData(new BigInteger("51"), BigInteger.ZERO, new BigInteger("115"));
    }

    @Test
    public void ne() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("115"));

        BooleanSeries s = $bigint("a").ne(new BigInteger("115")).eval(df);
        new BoolSeriesAsserts(s).expectData(true, true, false);
    }

    @Test
    public void eq() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("115"));

        BooleanSeries s = $bigint("a").eq(new BigInteger("115")).eval(df);
        new BoolSeriesAsserts(s).expectData(false, false, true);
    }

    @Test
    public void eq_Zero() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("0"),
                new BigInteger("115"));

        BooleanSeries s = $bigint("a").eq(BigInteger.ZERO).eval(df);
        new BoolSeriesAsserts(s).expectData(false, true, true, false);
    }

    @Test
    public void ne_NonNumber() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                new BigInteger("115"));

        BooleanSeries s = $bigint("a").ne("115").eval(df);
        new BoolSeriesAsserts(s).expectData(true, true, true);
    }

    @Test
    public void cumSum_getColumnName() {
        NumExp<?> exp = $bigint("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void sum_getColumnName() {
        NumExp<?> exp = $bigint("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }

    @Test
    public void castAsBool() {
        Condition c = $bigint(0).castAsBool();

        Series<BigInteger> s = Series.of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                null,
                new BigInteger("115"));
        new BoolSeriesAsserts(c.eval(s)).expectData(true, false, false, true);
    }

    @Test
    public void mapBoolVal() {
        Condition c = $bigint(0).mapBoolVal(d -> d.longValue() > 0);

        Series<BigInteger> s = Series.of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                null,
                new BigInteger("115"));
        new BoolSeriesAsserts(c.eval(s)).expectData(false, false, false, true);
    }

    @Test
    public void mapBool() {
        Condition c = $bigint(0).mapBool(Series::isNotNull);

        Series<BigInteger> s = Series.of(
                new BigInteger("-51"),
                BigInteger.ZERO,
                null,
                new BigInteger("115"));
        new BoolSeriesAsserts(c.eval(s)).expectData(true, true, false, true);
    }

    @Test
    public void between() {
        Condition c = $bigint("a").between(new BigInteger("1"), new BigInteger("3"));

        Series<BigInteger> s = Series.of(
                new BigInteger("0"),
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"));

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(s)).expectData(false, true, true, true, false);
    }

    @Test
    public void notBetween() {
        Condition c = $bigint("a").notBetween(new BigInteger("1"), new BigInteger("3"));

        Series<BigInteger> s = Series.of(
                new BigInteger("0"),
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"));

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(s)).expectData(true, false, false, false, true);
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $bigint("a"),
                $bigint("a"),
                $bigint("b"));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $bigint("a"),
                $bigint("a"),
                $bigint("b"));
    }
}
