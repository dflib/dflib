package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class SeriesGroupByTest {

    @Test
    public void toSeries() {
        SeriesGroupBy<String> gb = Series.of("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length());

        new SeriesAsserts(gb.toSeries()).expectData("a", "b", "e", "cd", "fg");
    }

    @Test
    public void agg() {

        Series<String> aggregated = Series.of("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length())
                .agg(Exp.$col("").vConcat("_"));

        new SeriesAsserts(aggregated).expectData("a_b_e", "cd_fg");
    }

    @Test
    public void aggMultiple() {

        DataFrame aggregated = Series.of("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length())
                .aggMultiple(
                        Exp.$col("first").first(),
                        Exp.$col("pipe").vConcat("|"),
                        Exp.$col("underscore").vConcat("_"));

        new DataFrameAsserts(aggregated, "first", "pipe", "underscore")
                .expectHeight(2)
                .expectRow(0, "a", "a|b|e", "a_b_e")
                .expectRow(1, "cd", "cd|fg", "cd_fg");
    }

    @Test
    public void aggMultiple_Named() {

        DataFrame aggregated = Series.of("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length())
                .aggMultiple(
                        Exp.$col("").first().as("f"),
                        Exp.$col("").vConcat("|").as("c1"),
                        Exp.$col("").vConcat("_").as("c2"));

        new DataFrameAsserts(aggregated, "f", "c1", "c2")
                .expectHeight(2)
                .expectRow(0, "a", "a|b|e", "a_b_e")
                .expectRow(1, "cd", "cd|fg", "cd_fg");
    }
}
