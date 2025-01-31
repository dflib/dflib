package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

public class RowSet_Expand_Merge_RowMapperTest {

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
                .merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) + 1)
                        .set(1, f.get(1) != null ? f.get(1).toString().toUpperCase() : null)
                        .set(2, f.get(2))
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(11)
                .expectRow(0, 2, "X1", "a")
                .expectRow(1, 2, "X2", "a")
                .expectRow(2, 3, "Y1", "b")
                .expectRow(3, 3, "Y2", "b")
                .expectRow(4, 5, "E1", "k")
                .expectRow(5, 5, "E2", "k")
                .expectRow(6, 1, "F1", "g")
                .expectRow(7, 1, "F2", "g")
                .expectRow(8, 2, "M1", "n")
                .expectRow(9, 2, "M2", "n")
                .expectRow(10, 6, null, "x");
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
                .rows(Series.ofBool(true, false, false, true, true, true))
                .expand("b")
                .merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) + 1)
                        .set(1, f.get(1) != null ? f.get(1).toString().toUpperCase() : null)
                        .set(2, f.get(2))
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(9)
                .expectRow(0, 2, "X1", "a")
                .expectRow(1, 2, "X2", "a")
                .expectRow(2, 2, List.of("y1", "y2"), "b")
                .expectRow(3, 4, List.of("e1", "e2"), "k")
                .expectRow(4, 1, "F1", "g")
                .expectRow(5, 1, "F2", "g")
                .expectRow(6, 2, "M1", "n")
                .expectRow(7, 2, "M2", "n")
                .expectRow(8, 6, null, "x");
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
                .merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) + 1)
                        .set(1, f.get(1) != null ? f.get(1).toString().toUpperCase() : null)
                        .set(2, f.get(2))
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(9)
                .expectRow(0, 2, "X1", "a")
                .expectRow(1, 2, "X2", "a")
                .expectRow(2, 2, List.of("y1", "y2"), "b")
                .expectRow(3, 4, List.of("e1", "e2"), "k")
                .expectRow(4, 1, "F1", "g")
                .expectRow(5, 1, "F2", "g")
                .expectRow(6, 2, "M1", "n")
                .expectRow(7, 2, "M2", "n")
                .expectRow(8, 6, null, "x");
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
                .rowsSample(2, new Random(9))
                .expand("b")
                .merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) + 1)
                        .set(1, f.get(1) != null ? f.get(1).toString().toUpperCase() : null)
                        .set(2, f.get(2))
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(8)
                .expectRow(0, 2, "X1", "a")
                .expectRow(1, 2, "X2", "a")
                .expectRow(2, 2, List.of("y1", "y2"), "b")
                .expectRow(3, 5, "E1", "k")
                .expectRow(4, 5, "E2", "k")
                .expectRow(5, 0, List.of("f1", "f2"), "g")
                .expectRow(6, 1, List.of("m1", "m2"), "n")
                .expectRow(7, 5, null, "x");
    }

}
