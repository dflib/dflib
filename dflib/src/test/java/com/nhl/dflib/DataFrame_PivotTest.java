package com.nhl.dflib;

import com.nhl.dflib.pivot.PivotBuilder;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_PivotTest {

    @Test
    public void testWithAggregation_2by2() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", 15.0,
                2, "y", 18.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().columns("b").rows("a").values("c", Exp.$double(0).sum());

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 15.0, 20.0)
                .expectRow(1, 2, null, 37.0);
    }

    @Test
    public void testWithAggregation_4_4() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", 1,
                3, "y", 2,
                2, "y", 3,
                4, "y", 4,
                2, "z", 5,
                2, "t", 6,
                2, "y", 7,
                1, "t", 8,
                4, "x", 9,
                1, "y", 10);


        DataFrame df = df1.pivot().columns("b").rows("a")
                .values("c", Exp.$int(0).sum())
                .sort("a", true);

        new DataFrameAsserts(df, "a", "x", "y", "z", "t")
                .expectHeight(4)
                .expectRow(0, 1, 1, 10, null, 8)
                .expectRow(1, 2, null, 10, 5, 6)
                .expectRow(2, 3, null, 2, null, null)
                .expectRow(3, 4, 9, 4, null, null);
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


        DataFrame df = df1.pivot().columns("b").rows("a").values("c", Exp.$double(0).sum());

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


        DataFrame df = df1.pivot().columns("b").rows("a").values("c", Exp.$double(0).sum());

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
                .expectRow(1, 2, null, 19.0);
    }

    @Test
    public void testNoAggregation_Dupes() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", 15.0,
                2, "y", 19.0,
                2, "y", 21.0,
                1, "y", 20.0);

        PivotBuilder pb = df1.pivot().columns("b").rows("a");
        assertThrows(IllegalArgumentException.class, () -> pb.values("c"));
    }

    @Test
    public void testWithAggregation_WithConverstion() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", 15.0,
                2, "y", 18.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().columns("b").rows("a").values("c", Exp.$double(0).sum().castAsDecimal().scale(2));

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, new BigDecimal("15.00"), new BigDecimal("20.00"))
                .expectRow(1, 2, null, new BigDecimal("37.00"));
    }
}
