package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Deprecated
public class Series_AggLegacyTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testAgg(SeriesType type) {
        String aggregated = type.createSeries("a", "b", "cd", "e", "fg").agg(SeriesAggregator.concat("_")).get(0);
        assertEquals("a_b_cd_e_fg", aggregated);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testAggMultiple(SeriesType type) {

        DataFrame aggregated = type.createSeries("a", "b", "cd", "e", "fg")
                .aggMultiple(
                        SeriesAggregator.first(),
                        SeriesAggregator.concat("|"),
                        SeriesAggregator.concat("_", "[", "]"),
                        SeriesAggregator.countInt());

        new DataFrameAsserts(aggregated, "first", "concat", "concat_", "countInt")
                .expectRow(0, "a", "a|b|cd|e|fg", "[a_b_cd_e_fg]", 5);
    }
}
