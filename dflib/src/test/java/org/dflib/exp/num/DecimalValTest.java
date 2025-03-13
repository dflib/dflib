package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.dflib.Exp.$decimal;
import static org.dflib.Exp.$decimalVal;

public class DecimalValTest {

    @Test
    void add() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        Series<BigDecimal> s = $decimalVal(new BigDecimal("3.33")).add($decimal("a")).eval(df);

        new SeriesAsserts(s).expectData(
                new BigDecimal("5.3400287"),
                new BigDecimal("7.83"));
    }

    @Test
    void add_Str() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        Series<BigDecimal> s = $decimalVal("3.33").add($decimal("a")).eval(df);

        new SeriesAsserts(s).expectData(
                new BigDecimal("5.3400287"),
                new BigDecimal("7.83"));
    }
}
