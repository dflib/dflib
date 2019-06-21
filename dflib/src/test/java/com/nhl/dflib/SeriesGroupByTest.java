package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class SeriesGroupByTest {

    @Test
    public void testToSeries() {
        SeriesGroupBy<String> gb = Series.forData("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length());

        new SeriesAsserts(gb.toSeries()).expectData("a", "b", "e", "cd", "fg");
    }

    @Test
    public void testAgg() {

        Series<String> aggregated = Series.forData("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length())
                .agg(SeriesAggregator.concat("_"));

        new SeriesAsserts(aggregated).expectData("a_b_e", "cd_fg");
    }

    @Test
    public void testAggMultiple() {

        DataFrame aggregated = Series.forData("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length())
                .aggMultiple(
                        SeriesAggregator.first(),
                        SeriesAggregator.concat("|"),
                        SeriesAggregator.concat("_"));

        new DataFrameAsserts(aggregated, "first", "concat", "concat_")
                .expectHeight(2)
                .expectRow(0, "a", "a|b|e", "a_b_e")
                .expectRow(1, "cd", "cd|fg", "cd_fg");
    }

    @Test
    public void testAggMultiple_Named() {

        DataFrame aggregated = Series.forData("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length())
                .aggMultiple(
                        SeriesAggregator.first().named("f"),
                        SeriesAggregator.concat("|").named("c1"),
                        SeriesAggregator.concat("_").named("c2"));

        new DataFrameAsserts(aggregated, "f", "c1", "c2")
                .expectHeight(2)
                .expectRow(0, "a", "a|b|e", "a_b_e")
                .expectRow(1, "cd", "cd|fg", "cd_fg");
    }
}
