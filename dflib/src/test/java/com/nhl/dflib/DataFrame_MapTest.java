package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_MapTest {

    @Test
    public void testMap_UnaryOp() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .map(p -> p.dropColumns("b"));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }

    @Test
    public void testMap() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .map(i1, (f, t) -> {
                    t.set(0, ((Integer) f.get(0)) * 10);
                    t.set(1, f.get(1));
                });

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testMap_Exp() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .map(Exp.$int(0).mul(10).as("a"), Exp.$col(1).as("b"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }
}
