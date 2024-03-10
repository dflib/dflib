package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.dflib.Exp.$col;

public class ColumnSet_AggCollectionsTest {

    @Test
    public void set() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.cols().agg($col("a").set(), $col(1).set());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, Set.of(1, 2), Set.of("x", "a"));
    }

    @Test
    public void list() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.cols().agg($col("a").list(), $col(1).list());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, List.of(1, 2, 1), List.of("x", "x", "a"));
    }
}
