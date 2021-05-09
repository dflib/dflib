package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_EvalTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").eval(Exp.concat(Exp.$col(""),", ", Exp.$col("")));
        new SeriesAsserts(s).expectData("x, x", "b, b", "c, c", "a, a");
    }
}
