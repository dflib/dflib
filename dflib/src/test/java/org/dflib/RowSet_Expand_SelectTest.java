package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

public class RowSet_Expand_SelectTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, List.of("x1", "x2"), "a",
                        2, List.of("y1", "y2"), "b",
                        4, List.of("e1", "e2"), "k",
                        0, List.of("f1", "f2"), "g",
                        1, List.of("m1", "m2"), "n",
                        5, null, "x")
                .rows()
                .expand("b")
                .select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(11)
                .expectRow(0, 1, "x1", "a")
                .expectRow(1, 1, "x2", "a")
                .expectRow(2, 2, "y1", "b")
                .expectRow(3, 2, "y2", "b")
                .expectRow(4, 4, "e1", "k")
                .expectRow(5, 4, "e2", "k")
                .expectRow(6, 0, "f1", "g")
                .expectRow(7, 0, "f2", "g")
                .expectRow(8, 1, "m1", "n")
                .expectRow(9, 1, "m2", "n")
                .expectRow(10, 5, null, "x");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, List.of("x1", "x2"), "a", // <--
                        2, List.of("y1", "y2"), "b",
                        4, List.of("e1", "e2"), "k",
                        0, List.of("f1", "f2"), "g", // <--
                        1, List.of("m1", "m2"), "n", // <--
                        5, null, "x") // <--
                .rows(Series.ofInt(0, 3, 4, 5))
                .expand("b")
                .select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(7)
                .expectRow(0, 1, "x1", "a")
                .expectRow(1, 1, "x2", "a")
                .expectRow(2, 0, "f1", "g")
                .expectRow(3, 0, "f2", "g")
                .expectRow(4, 1, "m1", "n")
                .expectRow(5, 1, "m2", "n")
                .expectRow(6, 5, null, "x");
    }

    @Test
    public void byIndex_AddRows() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, List.of("x1", "x2"), "a", // <--
                        2, List.of("y1", "y2"), "b",
                        4, List.of("e1", "e2"), "k", // <-- 2x
                        0, List.of("f1", "f2"), "g",
                        1, List.of("m1", "m2"), "n",
                        5, null, "x")

                .rows(0, 2, 2).expand("b")
                .select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(6)
                .expectRow(0, 1, "x1", "a")
                .expectRow(1, 1, "x2", "a")
                .expectRow(2, 4, "e1", "k")
                .expectRow(3, 4, "e2", "k")
                .expectRow(4, 4, "e1", "k")
                .expectRow(5, 4, "e2", "k");
    }

    @Test
    public void byRange() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, List.of("x1", "x2"), "a",
                        2, List.of("y1", "y2"), "b",
                        4, List.of("e1", "e2"), "k", // <--
                        0, List.of("f1", "f2"), "g", // <--
                        1, List.of("m1", "m2"), "n", // <--
                        5, null, "x")
                .rowsRange(2, 5).expand("b").select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(6)
                .expectRow(0, 4, "e1", "k")
                .expectRow(1, 4, "e2", "k")
                .expectRow(2, 0, "f1", "g")
                .expectRow(3, 0, "f2", "g")
                .expectRow(4, 1, "m1", "n")
                .expectRow(5, 1, "m2", "n");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, List.of("x1", "x2"), "a", // <--
                        2, List.of("y1", "y2"), "b",
                        4, List.of("e1", "e2"), "k",
                        0, List.of("f1", "f2"), "g", // <--
                        1, List.of("m1", "m2"), "n", // <--
                        5, null, "x") // <--
                .rows(Series.ofBool(true, false, false, true, true, true)).expand("b").select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(7)
                .expectRow(0, 1, "x1", "a")
                .expectRow(1, 1, "x2", "a")
                .expectRow(2, 0, "f1", "g")
                .expectRow(3, 0, "f2", "g")
                .expectRow(4, 1, "m1", "n")
                .expectRow(5, 1, "m2", "n")
                .expectRow(6, 5, null, "x");
    }


    @Test
    public void sample() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, List.of("x1", "x2"), "a",
                        2, List.of("y1", "y2"), "b",
                        4, List.of("e1", "e2"), "k",
                        0, List.of("f1", "f2"), "g",
                        1, List.of("m1", "m2"), "n",
                        5, null, "x")
                // using fixed seed to get reproducible result
                .rowsSample(2, new Random(9)).expand("b").select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, "x1", "a")
                .expectRow(1, 1, "x2", "a")
                .expectRow(2, 4, "e1", "k")
                .expectRow(3, 4, "e2", "k");
    }
}
