package com.nhl.dflib.exp.num;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.StrExp;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

public class DecimalColumnTest {

    @Test
    public void testGetColumnName() {
        assertEquals("a", $decimal("a").getColumnName());
        assertEquals("$decimal(0)", $decimal(0).getColumnName());
    }

    @Test
    public void testGetColumnName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $decimal("b").getColumnName(df));
        assertEquals("a", $decimal(0).getColumnName(df));
    }

    @Test
    public void testEval() {
        DecimalExp exp = $decimal("b");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", new BigDecimal("2.0100287"), new BigDecimal("2.010029"),
                "4", new BigDecimal("2.01001"), new BigDecimal("2.01003"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigDecimal("2.0100287"), new BigDecimal("2.01001"));
    }

    @Test
    public void testAs() {
        DecimalExp exp = $decimal("b");
        assertEquals("b", exp.getColumnName(mock(DataFrame.class)));
        assertEquals("c", exp.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void testScale() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        Series<BigDecimal> s = $decimal("a").scale(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void testCastAsDecimal() {
        DecimalExp e = $decimal("a");
        assertSame(e, e.castAsDecimal());
    }

    @Test
    public void testCastAsDecimal_DataFrame() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        Series<BigDecimal> s = $decimal("a").castAsDecimal().eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.0100287"), new BigDecimal("4.5"));
    }

    @Test
    public void testCastAsStr() {
        StrExp str = $decimal(0).castAsStr();
        Series<BigDecimal> s = Series.of(new BigDecimal("5.01"), null);
        new SeriesAsserts(str.eval(s)).expectData("5.01", null);
    }

    @Test
    public void testAdd_IntPrimitive() {

        DecimalExp exp = $decimal("a").add(1).scale(2);

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigDecimal("3.01"), new BigDecimal("5.50"));
    }

    @Test
    public void testCumSum() {
        DecimalExp exp = $decimal("a").cumSum();

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                null,
                new BigDecimal("2.01"),
                new BigDecimal("4.59"),
                null,
                new BigDecimal("11.21"),
                new BigDecimal("-12.16"));

        new SeriesAsserts(exp.eval(df)).expectData(
                null,
                new BigDecimal("2.01"),
                new BigDecimal("6.60"),
                null,
                new BigDecimal("17.81"),
                new BigDecimal("5.65")
        );
    }

    @Test
    public void testSum() {
        DecimalExp exp = $decimal("a").sum().scale(2);

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigDecimal("6.51"));
    }

    @Test
    public void testSum_Nulls() {
        DecimalExp exp = $decimal("a").sum().scale(2);

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                null,
                new BigDecimal("4.5"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigDecimal("6.51"));
    }

    @Test
    public void testMedian_Odd() {
        DecimalExp exp = $decimal(0).median().scale(3);

        Series<BigDecimal> s = Series.of(new BigDecimal("100.01"), new BigDecimal("55.5"), new BigDecimal("0."));

        new SeriesAsserts(exp.eval(s)).expectData(new BigDecimal("55.500"));
    }

    @Test
    public void testMedian_Even() {
        DecimalExp exp = $decimal(0).median().scale(1);

        Series<BigDecimal> s = Series.of(
                new BigDecimal("100.01"), new BigDecimal("55.5"), new BigDecimal("0."), new BigDecimal("5."));

        new SeriesAsserts(exp.eval(s)).expectData(new BigDecimal("30.3"));
    }

    @Test
    public void testMedian_Nulls() {
        DecimalExp exp = $decimal(0).median().scale(3);

        Series<BigDecimal> s = Series.of(
                new BigDecimal("100.01"), null, new BigDecimal("55.5"), new BigDecimal("0."));

        new SeriesAsserts(exp.eval(s)).expectData(new BigDecimal("55.500"));
    }

    @Test
    public void testAdd_Decimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("1.01"), new BigDecimal("2."),
                new BigDecimal("3."), new BigDecimal("4.5"));

        Series<? extends Number> s = $decimal("b").add($decimal("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("3.01"), new BigDecimal("7.5"));
    }

    @Test
    public void testDivide_Int() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("35"), 2,
                new BigDecimal("3.3"), 3);

        Series<? extends Number> s = $decimal("a").div($int("b")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("17.5"), new BigDecimal("1.1"));
    }

    @Test
    public void testDivide_Double() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("5.0"), 2.5,
                new BigDecimal("3.3"), 3.33);

        Series<? extends Number> s = $decimal("a").div($double("b")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2"), new BigDecimal("0.99099099099099096984560210653599037467692134485397"));
    }


    @Test
    public void testGT_Decimal() {
        Condition c = $decimal("a").gt($decimal("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("1.1"), new BigDecimal("1.0001"),
                new BigDecimal("3"), new BigDecimal("3"),
                new BigDecimal("1.1"), new BigDecimal("1.2"));

        new BooleanSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void testAbs() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("-5.1"),
                BigDecimal.ZERO,
                new BigDecimal("11.5"));

        Series<? extends Number> s = $decimal("a").abs().eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("5.1"), BigDecimal.ZERO, new BigDecimal("11.5"));
    }

    @Test
    public void testNe() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("-5.1"),
                BigDecimal.ZERO,
                new BigDecimal("11.5"));

        BooleanSeries s = $decimal("a").ne(new BigDecimal("11.5")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, true, false);
    }

    @Test
    public void testEq() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("-5.1"),
                BigDecimal.ZERO,
                new BigDecimal("11.5"));

        BooleanSeries s = $decimal("a").eq(new BigDecimal("11.5")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, false, true);
    }

    @Test
    public void testEq_Zero() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("-5.1"),
                BigDecimal.ZERO,
                new BigDecimal("0"),
                new BigDecimal("11.5"));

        BooleanSeries s = $decimal("a").eq(BigDecimal.ZERO).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true, true, false);
    }

    @Test
    public void testNe_NonNumber() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("-5.1"),
                BigDecimal.ZERO,
                new BigDecimal("11.5"));

        BooleanSeries s = $decimal("a").ne("11.5").eval(df);
        new BooleanSeriesAsserts(s).expectData(true, true, true);
    }

    @Test
    public void testCumSum_getColumnName() {
        NumExp<?> exp = $decimal("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void testSum_getColumnName() {
        NumExp<?> exp = $decimal("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }

    @Test
    public void testCastAsCondition() {
        Condition c = $decimal(0).castAsCondition();

        Series<BigDecimal> s = Series.of(
                new BigDecimal("-5.1"),
                BigDecimal.ZERO,
                null,
                new BigDecimal("11.5"));
        new BooleanSeriesAsserts(c.eval(s)).expectData(true, false, false, true);
    }

    @Test
    public void testMapConditionVal() {
        Condition c = $decimal(0).mapConditionVal(d -> d.doubleValue() > 0);

        Series<BigDecimal> s = Series.of(
                new BigDecimal("-5.1"),
                BigDecimal.ZERO,
                null,
                new BigDecimal("11.5"));
        new BooleanSeriesAsserts(c.eval(s)).expectData(false, false, false, true);
    }

    @Test
    public void testMapCondition() {
        Condition c = $decimal(0).mapCondition(Series::isNotNull);

        Series<BigDecimal> s = Series.of(
                new BigDecimal("-5.1"),
                BigDecimal.ZERO,
                null,
                new BigDecimal("11.5"));
        new BooleanSeriesAsserts(c.eval(s)).expectData(true, true, false, true);
    }
}
