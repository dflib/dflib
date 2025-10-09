package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.dflib.Exp.$double;
import static org.dflib.Exp.$int;

@Deprecated
public class DataFrame_PivotAggExpTest {

    @Test
    public void withAggregation_2by2() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", 15.0,
                2, "y", 18.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().cols("b").rows("a").vals("c", $double(0).sum());

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 15.0, 20.0)
                .expectRow(1, 2, null, 37.0);
    }

    @Test
    public void withAggregation_4_4() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
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


        DataFrame df = df1.pivot().cols("b").rows("a")
                .vals("c", $int(0).sum())
                .sort("a", true);

        new DataFrameAsserts(df, "a", "x", "y", "z", "t")
                .expectHeight(4)
                .expectRow(0, 1, 1L, 10L, null, 8L)
                .expectRow(1, 2, null, 10L, 5L, 6L)
                .expectRow(2, 3, null, 2L, null, null)
                .expectRow(3, 4, 9L, 4L, null, null);
    }

    @Test
    // "TODO: allow nulls as group by keys"
    @Disabled("TODO: allow nulls as group by keys")
    @DisplayName("Rows with null values in row columns should be included")
    public void withAggregation_NullsInPivotRows() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", 15.0,
                null, "y", 18.0,
                null, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().cols("b").rows("a").vals("c", $double(0).sum());

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 15.0, 20.0)
                .expectRow(1, null, null, 37.0);
    }

    @Test
    @DisplayName("Rows with null values in pivot columns are skipped, as we can't have null column labels")
    public void withAggregation_NullInPivotColumns() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, null, 15.0,
                2, "y", 18.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().cols("b").rows("a").vals("c", $double(0).sum());

        new DataFrameAsserts(df, "a", "y")
                .expectHeight(2)
                .expectRow(0, 2, 37.0)
                .expectRow(1, 1, 20.0);
    }


    @Test
    public void withAggregation_WithConversion() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", 15.0,
                2, "y", 18.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().cols("b").rows("a").vals("c", $double(0).sum().castAsDecimal().scale(2));

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, new BigDecimal("15.00"), new BigDecimal("20.00"))
                .expectRow(1, 2, null, new BigDecimal("37.00"));
    }
}
