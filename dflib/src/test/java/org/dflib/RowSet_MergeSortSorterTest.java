package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;

public class RowSet_MergeSortSorterTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        4, "e", "k",
                        0, "f", "g",
                        1, "m", "n")
                .rows()
                .sort(Exp.$int("a").asc(), Exp.$str("b").desc())
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 0, "f", "g")
                .expectRow(1, 1, "x", "a")
                .expectRow(2, 1, "m", "n")
                .expectRow(3, 2, "y", "b")
                .expectRow(4, 4, "e", "k");
    }

    @Test
    public void all_StrSorter() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        4, "e", "k",
                        0, "f", "g",
                        1, "m", "n")
                .rows()
                .sort("a asc, b desc")
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 0, "f", "g")
                .expectRow(1, 1, "x", "a")
                .expectRow(2, 1, "m", "n")
                .expectRow(3, 2, "y", "b")
                .expectRow(4, 4, "e", "k");
    }

    @Test
    public void all_overTransformedColumn() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        -1, "x", "a",
                        2, "y", "b",
                        4, "e", "k",
                        0, "f", "g",
                        1, "m", "n")
                .rows()
                .sort(Exp.$int("a").asc())
                .merge(
                        $int("a").mapVal(i -> -i),
                        $col("b"),
                        $col("c")
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, -4, "e", "k" )
                .expectRow(1, -2, "y", "b")
                .expectRow(2, -1, "m", "n")
                .expectRow(3, 0, "f", "g")
                .expectRow(4, 1, "x", "a");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a", // <--
                        2, "y", "b",
                        4, "e", "k",
                        0, "f", "g", // <--
                        1, "m", "n") // <--
                .rows(Series.ofInt(0, 3, 4))
                .sort(Exp.$int("a").asc(), Exp.$str("b").desc())
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 0, "f", "g")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 4, "e", "k")
                .expectRow(3, 1, "x", "a")
                .expectRow(4, 1, "m", "n");
    }

    @Test
    public void byRange() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b", // <--
                        4, "e", "k", // <--
                        0, "f", "g", // <--
                        1, "m", "n")
                .rowsRange(1, 4)
                .sort(Exp.$int("a").asc(), Exp.$str("b").desc())
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 0, "f", "g")
                .expectRow(2, 2, "y", "b")
                .expectRow(3, 4, "e", "k")
                .expectRow(4, 1, "m", "n");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a", // <--
                        2, "y", "b",
                        4, "e", "k",
                        0, "f", "g", // <--
                        1, "m", "n") // <--
                .rows(Series.ofBool(true, false, false, true, true))
                .sort(Exp.$int("a").asc(), Exp.$str("b").desc())
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 0, "f", "g")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 4, "e", "k")
                .expectRow(3, 1, "x", "a")
                .expectRow(4, 1, "m", "n");
    }

    @Test
    public void sample() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        4, "e", "k",
                        0, "f", "g",
                        1, "m", "n")
                .rowsSample(3, new Random(9))
                .sort(Exp.$int("a").asc(), Exp.$str("b").desc())
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 0, "f", "g")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "x", "a")
                .expectRow(3, 4, "e", "k")
                .expectRow(4, 1, "m", "n");
    }
}
