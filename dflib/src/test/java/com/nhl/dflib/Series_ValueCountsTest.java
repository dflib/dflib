package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_ValueCountsTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testValueCounts(SeriesType type) {
        DataFrame counts = type.createSeries("a", "b", "a", "a", "c").valueCounts();

        new DataFrameAsserts(counts, "value", "count")
                .expectHeight(3)
                .expectRow(0, "a", 3)
                .expectRow(1, "b", 1)
                .expectRow(2, "c", 1);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testValueCounts_Nulls(SeriesType type) {
        DataFrame counts = type.createSeries("a", "b", "a", "a", null, "c").valueCounts();

        new DataFrameAsserts(counts, "value", "count")
                .expectHeight(3)
                .expectRow(0, "a", 3)
                .expectRow(1, "b", 1)
                .expectRow(2, "c", 1);

    }
}
