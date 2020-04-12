package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_MapTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testMap_Value(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").map(String::toUpperCase);
        new SeriesAsserts(s).expectData("A", "B", "C");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testMap_DataFrame(SeriesType type) {
        DataFrame df = type.createSeries("a", "b", "c").map(Index.forLabels("upper", "is_c"), (v, r) -> {
            r.set(0, v.toUpperCase());
            r.set(1, v.equals("c"));
        });

        new DataFrameAsserts(df, "upper", "is_c")
                .expectHeight(3)
                .expectRow(0, "A", false)
                .expectRow(1, "B", false)
                .expectRow(2, "C", true);
    }

}
