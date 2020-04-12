package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesGroupByAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_GroupTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testGroup(SeriesType type) {
        SeriesGroupBy<Integer> g = type.createSeries(1, 5, 5, 8, 5).group();
        new SeriesGroupByAsserts(g)
                .expectGroups(1, 5, 8)
                .expectGroupData(1, 1)
                .expectGroupData(5, 5, 5, 5)
                .expectGroupData(8, 8);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testGroup_SkipNulls(SeriesType type) {
        SeriesGroupBy<Integer> g = type.createSeries(8, null, 5, 8, 5, null).group();
        new SeriesGroupByAsserts(g)
                .expectGroups(8, 5)
                .expectGroupData(5, 5, 5)
                .expectGroupData(8, 8, 8);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testGroup_WithHash(SeriesType type) {
        SeriesGroupBy<Integer> g = type.createSeries(1, 16, 5, 8, 7).group((Integer i) -> i % 2);
        new SeriesGroupByAsserts(g)
                .expectGroups(0, 1)
                .expectGroupData(0, 16, 8)
                .expectGroupData(1, 1, 5, 7);
    }
}
