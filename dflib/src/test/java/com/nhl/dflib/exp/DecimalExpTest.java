package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.nhl.dflib.Exp.$decimal;
import static org.junit.jupiter.api.Assertions.assertSame;

public class DecimalExpTest {

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
    public void testAdd_Scale() {

        DecimalExp exp = $decimal("a").add(1).scale(2);

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigDecimal("3.01"), new BigDecimal("5.50"));
    }

    @Test
    public void testSum_Scale() {
        DecimalExp exp = $decimal("a").sum().scale(2);

        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        new SeriesAsserts(exp.eval(df)).expectData(new BigDecimal("6.51"));
    }

    @Test
    public void testMedian_Odd_Scale() {
        DecimalExp exp = $decimal(0).median().scale(3);

        Series<BigDecimal> s = Series.forData(new BigDecimal("100.01"), new BigDecimal("55.5"), new BigDecimal("0."));

        new SeriesAsserts(exp.eval(s)).expectData(new BigDecimal("55.500"));
    }

    @Test
    public void testMedian_Even_Scale() {
        DecimalExp exp = $decimal(0).median().scale(1);

        Series<BigDecimal> s = Series.forData(
                new BigDecimal("100.01"), new BigDecimal("55.5"), new BigDecimal("0."), new BigDecimal("5."));

        new SeriesAsserts(exp.eval(s)).expectData(new BigDecimal("30.3"));
    }
}
