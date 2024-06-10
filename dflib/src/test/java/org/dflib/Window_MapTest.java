package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;

public class Window_MapTest {

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        DataFrame r = df.over().cols("a", "d").map($col("a"), $int("a").sum());
        new DataFrameAsserts(r, "a", "b", "c", "d").expectHeight(0);
    }

    @Test
    public void cols_Implicit() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over().map($int("a").sum());
        new DataFrameAsserts(r, "a", "b", "sum(a)")
                .expectHeight(5)
                .expectRow(0, 1, "x", 5)
                .expectRow(1, 2, "y", 5)
                .expectRow(2, 1, "z", 5)
                .expectRow(3, 0, "a", 5)
                .expectRow(4, 1, "x", 5);
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
                .cols("b", "c")
                .map(
                        $col("b").first(),
                        $int("a").sum()
                );

        new DataFrameAsserts(r, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 1, "x", 5)
                .expectRow(1, 2, "x", 5)
                .expectRow(2, 1, "x", 5)
                .expectRow(3, 0, "x", 5)
                .expectRow(4, 1, "x", 5);
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
                .cols(1, 2)
                .map(
                        $col("b").first(),
                        $int("a").sum()
                );

        new DataFrameAsserts(r, "a", "b", "2")
                .expectHeight(5)
                .expectRow(0, 1, "x", 5)
                .expectRow(1, 2, "x", 5)
                .expectRow(2, 1, "x", 5)
                .expectRow(3, 0, "x", 5)
                .expectRow(4, 1, "x", 5);
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
                .map($int("a").sum());

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5, "x")
                .expectRow(1, 5, "y")
                .expectRow(2, 5, "z")
                .expectRow(3, 5, "a")
                .expectRow(4, 5, "x");
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
                .map($int("a").sum());

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5, "x")
                .expectRow(1, 5, "y")
                .expectRow(2, 5, "z")
                .expectRow(3, 5, "a")
                .expectRow(4, 5, "x");
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
                .map($int("a").sum());

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5, "x")
                .expectRow(1, 5, "y")
                .expectRow(2, 5, "z")
                .expectRow(3, 5, "a")
                .expectRow(4, 5, "x");
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
                .map($int("a").sum());

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5, "x")
                .expectRow(1, 5, "y")
                .expectRow(2, 5, "z")
                .expectRow(3, 5, "a")
                .expectRow(4, 5, "x");
    }

}
