package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RowColumnSet_Expand_SelectTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b", "c")
            .of(
                    1, List.of("x1", "x2"), "a",
                    2, List.of("y1", "y2"), "b",
                    4, List.of("e1", "e2"), "k",
                    0, List.of("f1", "f2"), "g",
                    1, List.of("m1", "m2"), "n",
                    5, null, "x");

    @Test
    public void all() {
        DataFrame df = TEST_DF
                .rows().expand("b")
                .cols()
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
    public void rowsAll_colsByName() {
        DataFrame df = TEST_DF
                .rows().expand("b")
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(11)
                .expectRow(0, "x1", 1)
                .expectRow(1, "x2", 1)
                .expectRow(2, "y1", 2)
                .expectRow(3, "y2", 2)
                .expectRow(4, "e1", 4)
                .expectRow(5, "e2", 4)
                .expectRow(6, "f1", 0)
                .expectRow(7, "f2", 0)
                .expectRow(8, "m1", 1)
                .expectRow(9, "m2", 1)
                .expectRow(10, null, 5);
    }

    @Test
    public void rowByIndex_colsByName() {
        DataFrame df = TEST_DF
                .rows(0, 3, 4, 5).expand("b")
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(7)
                .expectRow(0, "x1", 1)
                .expectRow(1, "x2", 1)
                .expectRow(2, "f1", 0)
                .expectRow(3, "f2", 0)
                .expectRow(4, "m1", 1)
                .expectRow(5, "m2", 1)
                .expectRow(6, null, 5);
    }

    @Test
    public void rowsByIndex_colsByName_AddRows_AddCols() {
        DataFrame df = TEST_DF
                .rows(0, 2, 2).expand("b")
                .cols("b", "a", "x")
                .select();

        new DataFrameAsserts(df, "b", "a", "x")
                .expectHeight(6)
                .expectRow(0, "x1", 1, null)
                .expectRow(1, "x2", 1, null)
                .expectRow(2, "e1", 4, null)
                .expectRow(3, "e2", 4, null)
                .expectRow(4, "e1", 4, null)
                .expectRow(5, "e2", 4, null);
    }

    @Test
    public void rowByRange_colsByName() {
        DataFrame df = TEST_DF
                .rowsRange(2, 5).expand("b")
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(6)
                .expectRow(0, "e1", 4)
                .expectRow(1, "e2", 4)
                .expectRow(2, "f1", 0)
                .expectRow(3, "f2", 0)
                .expectRow(4, "m1", 1)
                .expectRow(5, "m2", 1);
    }


    @Test
    public void rowsByCondition_colsByName() {
        DataFrame df = TEST_DF
                .rows(Series.ofBool(true, false, false, true, true, true)).expand("b")
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(7)
                .expectRow(0, "x1", 1)
                .expectRow(1, "x2", 1)
                .expectRow(2, "f1", 0)
                .expectRow(3, "f2", 0)
                .expectRow(4, "m1", 1)
                .expectRow(5, "m2", 1)
                .expectRow(6, null, 5);
    }
}
