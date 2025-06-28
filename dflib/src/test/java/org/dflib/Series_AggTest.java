package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.dflib.Exp.*;

public class Series_AggTest {

    @Deprecated
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void agg(SeriesType type) {
        String aggregated = type.createSeries("a", "b", "cd", "e", "fg").agg(Exp.$col("").vConcat("_")).get(0);
        assertEquals("a_b_cd_e_fg", aggregated);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void reduce(SeriesType type) {
        String aggregated = type.createSeries("a", "b", "cd", "e", "fg").reduce(Exp.$col("").vConcat("_"));
        assertEquals("a_b_cd_e_fg", aggregated);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void reduce_StrExp(SeriesType type) {
        String aggregated = type.createSeries("a", "b", "cd", "e", "fg").reduce("max(str(0))");
        assertEquals("fg", aggregated);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void aggMultiple(SeriesType type) {

        DataFrame aggregated = type.createSeries("a", "b", "cd", "e", "fg")
                .aggMultiple(
                        $col(0).first().as("first"),
                        $col(0).vConcat("|").as("concat"),
                        $col(0).vConcat("_", "[", "]").as("concat"),
                        count().as("count"));

        new DataFrameAsserts(aggregated, "first", "concat", "concat_", "count")
                .expectRow(0, "a", "a|b|cd|e|fg", "[a_b_cd_e_fg]", 5);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void aggMultiple_StrExp(SeriesType type) {

        DataFrame aggregated = type.createSeries("a", "b", "cd", "e", "fg")
                .aggMultiple(
                        "a",
                        "count()");

        new DataFrameAsserts(aggregated, "a", "count")
                .expectRow(0, "a", 5);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void first(SeriesType type) {
        String f1 = type.createSeries("a", "b", "cd", "e", "fg").first();
        assertEquals("a", f1);

        Object f2 = type.createSeries().first();
        assertNull(f2);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void concat(SeriesType type) {
        String concat = type.createSeries("a", "b", "cd", "e", "fg").concat("_");
        assertEquals("a_b_cd_e_fg", concat);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void concat_PrefixSuffix(SeriesType type) {
        String concat = type.createSeries("a", "b", "cd", "e", "fg").concat("_", "[", "]");
        assertEquals("[a_b_cd_e_fg]", concat);
    }
}
