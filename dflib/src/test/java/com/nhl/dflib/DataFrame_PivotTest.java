package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DataFrame_PivotTest {

    @Test
    public void testWithAggregation() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", 15.0,
                2, "y", 18.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().columns("b").rows("a").values("c", SeriesAggregator.sumDouble());

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 15.0, 20.0)
                .expectRow(1, 2, null, 37.0);
    }

    @Test
    // "TODO: allow nulls as group by keys"
    @Disabled("TODO: allow nulls as group by keys")
    @DisplayName("Rows with null values in row columns should be included")
    public void testWithAggregation_NullsInPivotRows() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", 15.0,
                null, "y", 18.0,
                null, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().columns("b").rows("a").values("c", SeriesAggregator.sumDouble());

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 15.0, 20.0)
                .expectRow(1, null, null, 37.0);
    }

    @Test
    @DisplayName("Rows with null values in pivot columns are skipped, as we can't have null column labels")
    public void testWithAggregation_NullInPivotColumns() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, null, 15.0,
                2, "y", 18.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().columns("b").rows("a").values("c", SeriesAggregator.sumDouble());

        new DataFrameAsserts(df, "a", "y")
                .expectHeight(2)
                .expectRow(0, 2, 37.0)
                .expectRow(1, 1, 20.0);
    }

    @Test
    public void testNoAggregation() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", 15.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().columns("b").rows("a").values("c");

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 15.0, 20.0)
                .expectRow(1, 2, null, 19);
    }
}
