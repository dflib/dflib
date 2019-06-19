package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesGroupByAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_GroupTest extends BaseObjectSeriesTest {

    public Series_GroupTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testGroup() {
        SeriesGroupBy<Integer> g = createSeries(1, 5, 5, 8, 5).group();
        new SeriesGroupByAsserts(g)
                .expectGroups(1, 5, 8)
                .expectGroupData(1, 1)
                .expectGroupData(5, 5, 5, 5)
                .expectGroupData(8, 8);
    }

    @Test
    public void testGroup_SkipNulls() {
        SeriesGroupBy<Integer> g = createSeries(8, null, 5, 8, 5, null).group();
        new SeriesGroupByAsserts(g)
                .expectGroups(8, 5)
                .expectGroupData(5, 5, 5)
                .expectGroupData(8, 8, 8);
    }

    @Test
    public void testGroup_WithHash() {
        SeriesGroupBy<Integer> g = createSeries(1, 16, 5, 8, 7).group((Integer i) -> i % 2);
        new SeriesGroupByAsserts(g)
                .expectGroups(0, 1)
                .expectGroupData(0, 16, 8)
                .expectGroupData(1, 1, 5, 7);
    }
}
