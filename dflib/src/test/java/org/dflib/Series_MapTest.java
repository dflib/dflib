package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.concat;

public class Series_MapTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void exp(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").map(concat(Exp.$col(""),", ", $col("")));
        new SeriesAsserts(s).expectData("x, x", "b, b", "c, c", "a, a");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void valueMapper(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").map(String::toUpperCase);
        new SeriesAsserts(s).expectData("A", "B", "C");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void toDataFrame(SeriesType type) {
        DataFrame df = type.createSeries("a", "b", "c").map(Index.of("upper", "is_c"), (v, r) -> r
                .set(0, v.toUpperCase())
                .set(1, v.equals("c")));

        new DataFrameAsserts(df, "upper", "is_c")
                .expectHeight(3)
                .expectRow(0, "A", false)
                .expectRow(1, "B", false)
                .expectRow(2, "C", true);
    }

}
