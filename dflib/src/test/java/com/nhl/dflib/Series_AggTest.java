package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class Series_AggTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testAgg(SeriesType type) {
        String aggregated = type.createSeries("a", "b", "cd", "e", "fg").agg(Exp.$col("").vConcat("_")).get(0);
        assertEquals("a_b_cd_e_fg", aggregated);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testAggMultiple(SeriesType type) {

        DataFrame aggregated = type.createSeries("a", "b", "cd", "e", "fg")
                .aggMultiple(
                        Exp.$col("first").first(),
                        Exp.$col("concat").vConcat("|"),
                        Exp.$col("concat").vConcat("_", "[", "]"),
                        Exp.count().as("count"));

        new DataFrameAsserts(aggregated, "first", "concat", "concat_", "count")
                .expectRow(0, "a", "a|b|cd|e|fg", "[a_b_cd_e_fg]", 5);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testFirst(SeriesType type) {
        String f1 = type.createSeries("a", "b", "cd", "e", "fg").first();
        assertEquals("a", f1);

        Object f2 = type.createSeries().first();
        assertNull(f2);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testConcat(SeriesType type) {
        String concat = type.createSeries("a", "b", "cd", "e", "fg").concat("_");
        assertEquals("a_b_cd_e_fg", concat);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testConcat_PrefixSuffix(SeriesType type) {
        String concat = type.createSeries("a", "b", "cd", "e", "fg").concat("_", "[", "]");
        assertEquals("[a_b_cd_e_fg]", concat);
    }
}
