package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RowColumnSet_Expand_MergeTest {

    static final DataFrame EMPTY_TEST_DF = DataFrame.foldByRow("a", "b", "c").of();

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b", "c")
            .of(
                    1, List.of("x1", "x2"), "a",
                    2, List.of("y1", "y2"), "b",
                    4, List.of("e1", "e2"), "k",
                    0, List.of("f1", "f2"), "g",
                    1, List.of("m1", "m2"), "n",
                    5, null, "x");

    @Test
    public void empty() {
        DataFrame df = EMPTY_TEST_DF
                .rows().expand("b")
                .cols()
                .merge();

        new DataFrameAsserts(df, "a", "b", "c").expectHeight(0);
    }

    @Test
    public void all() {
        DataFrame df = TEST_DF
                .rows().expand("b")
                .cols()
                .merge();

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
    public void rowsAll_colsByName() {
        DataFrame df = TEST_DF
                .rows().expand("b")
                .cols("b", "a")
                .merge();

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
    public void rowByIndex_colsByName() {
        DataFrame df = TEST_DF
                .rows(0, 3, 4, 5).expand("b")
                .cols("b", "a")
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(9)
                .expectRow(0, 1, "x1", "a")
                .expectRow(1, 1, "x2", "a")
                .expectRow(2, 2, List.of("y1", "y2"), "b")
                .expectRow(3, 4, List.of("e1", "e2"), "k")
                .expectRow(4, 0, "f1", "g")
                .expectRow(5, 0, "f2", "g")
                .expectRow(6, 1, "m1", "n")
                .expectRow(7, 1, "m2", "n")
                .expectRow(8, 5, null, "x");
    }

    @Test
    public void rowsByIndex_colsByName_AddRows_AddCols() {
        DataFrame df = TEST_DF
                .rows(0, 2, 2).expand("b")
                .cols("b", "a", "x")
                .merge();

        new DataFrameAsserts(df, "a", "b", "c", "x")
                .expectHeight(10)
                .expectRow(0, 1, "x1", "a", null)
                .expectRow(1, 1, "x2", "a", null)
                .expectRow(2, 2, List.of("y1", "y2"), "b", null)
                .expectRow(3, 4, "e1", "k", null)
                .expectRow(4, 4, "e2", "k", null)
                .expectRow(5, 0,  List.of("f1", "f2"), "g", null)
                .expectRow(6, 1,  List.of("m1", "m2"), "n", null)
                .expectRow(7, 5, null, "x", null)

                // TODO: is this logically correct? These rows was added by duplicating and expanding row 2, yet column
                //  "c" is not in the column set, so should it be "null" instead of "b"?
                .expectRow(8, 4, "e1", "k", null)
                .expectRow(9, 4, "e2", "k", null);
    }

    @Test
    public void rowByRange_colsByName() {
        DataFrame df = TEST_DF
                .rowsRange(2, 5).expand("b")
                .cols("b", "a")
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(9)
                .expectRow(0, 1, List.of("x1", "x2"), "a")
                .expectRow(1, 2, List.of("y1", "y2"), "b")
                .expectRow(2, 4, "e1", "k")
                .expectRow(3, 4, "e2", "k")
                .expectRow(4, 0, "f1", "g")
                .expectRow(5, 0, "f2", "g")
                .expectRow(6, 1, "m1", "n")
                .expectRow(7, 1, "m2", "n")
                .expectRow(8, 5, null, "x");
    }

    @Test
    public void rowsByCondition_colsByName() {
        DataFrame df = TEST_DF
                .rows(Series.ofBool(true, false, false, true, true, true)).expand("b")
                .cols("b", "a")
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(9)
                .expectRow(0, 1, "x1", "a")
                .expectRow(1, 1, "x2", "a")
                .expectRow(2, 2, List.of("y1", "y2"), "b")
                .expectRow(3, 4, List.of("e1", "e2"), "k")
                .expectRow(4, 0, "f1", "g")
                .expectRow(5, 0, "f2", "g")
                .expectRow(6, 1, "m1", "n")
                .expectRow(7, 1, "m2", "n")
                .expectRow(8, 5, null, "x");
    }
}
