package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.dflib.Exp.*;

public class Series_EvalTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").eval(concat(Exp.$col(""),", ", $col("")));
        new SeriesAsserts(s).expectData("x, x", "b, b", "c, c", "a, a");
    }
}
