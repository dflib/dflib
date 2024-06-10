package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Window_SelectTest {

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        DataFrame r = df.over().select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void cols_Implicit() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over().select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)")
                .expectHeight(5)
                .expectRow(0, 5)
                .expectRow(1, 5)
                .expectRow(2, 5)
                .expectRow(3, 5)
                .expectRow(4, 5);
    }

    @Test
    public void cols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .cols("a", "b")
                .select(
                        $int("a").sum(),
                        $col("b").first()
                );

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5, "x")
                .expectRow(1, 5, "x")
                .expectRow(2, 5, "x")
                .expectRow(3, 5, "x")
                .expectRow(4, 5, "x");
    }

    @Test
    public void cols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .cols(0, 1)
                .select(
                        $int("a").sum(),
                        $col("b").first()
                );

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5, "x")
                .expectRow(1, 5, "x")
                .expectRow(2, 5, "x")
                .expectRow(3, 5, "x")
                .expectRow(4, 5, "x");
    }

    @Test
    public void cols_ByPredicate() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .cols("a"::equals)
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5)
                .expectRow(1, 5)
                .expectRow(2, 5)
                .expectRow(3, 5)
                .expectRow(4, 5);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .colsExcept("b")
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5)
                .expectRow(1, 5)
                .expectRow(2, 5)
                .expectRow(3, 5)
                .expectRow(4, 5);
    }

    @Test
    public void colsExcept_ByIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .colsExcept(1)
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5)
                .expectRow(1, 5)
                .expectRow(2, 5)
                .expectRow(3, 5)
                .expectRow(4, 5);
    }

    @Test
    public void colsExcept_ByPredicate() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .colsExcept("b"::equals)
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5)
                .expectRow(1, 5)
                .expectRow(2, 5)
                .expectRow(3, 5)
                .expectRow(4, 5);
    }

}
