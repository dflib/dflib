package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowColumnSet_Unique_SelectTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b", "c")
            .of(
                    1, "x1", "a",
                    2, "y1", "b",
                    1, "e1", "k",
                    0, "f1", "g",
                    1, "m1", "n",
                    5, null, "x");

    @Test
    public void all() {
        DataFrame df = TEST_DF
                .rows().unique("a")
                .cols()
                .select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, "x1", "a")
                .expectRow(1, 2, "y1", "b")
                .expectRow(2, 0, "f1", "g")
                .expectRow(3, 5, null, "x");
    }

    @Test
    public void rowsAll_colsByName() {
        DataFrame df = TEST_DF
                .rows().unique("a")
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(4)
                .expectRow(0, "x1", 1)
                .expectRow(1, "y1", 2)
                .expectRow(2, "f1", 0)
                .expectRow(3, null, 5);
    }

    @Test
    public void rowByIndex_colsByName() {
        DataFrame df = TEST_DF
                .rows(0, 3, 4, 5).unique("a")
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(3)
                .expectRow(0, "x1", 1)
                .expectRow(1, "f1", 0)
                .expectRow(2, null, 5);
    }

    @Test
    public void rowsByIndex_colsByName_AddRows_AddCols() {
        DataFrame df = TEST_DF
                .rows(0, 2, 2).unique("a")
                .cols("b", "a", "x")
                .select();

        new DataFrameAsserts(df, "b", "a", "x")
                .expectHeight(1)
                .expectRow(0, "x1", 1, null);
    }

    @Test
    public void rowByRange_colsByName() {
        DataFrame df = TEST_DF
                .rowsRange(2, 5).unique("a")
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "e1", 1)
                .expectRow(1, "f1", 0);
    }

    @Test
    public void rowsByCondition_colsByName() {
        DataFrame df = TEST_DF
                .rows(Series.ofBool(true, false, false, true, true, true)).unique("a")
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(3)
                .expectRow(0, "x1", 1)
                .expectRow(1, "f1", 0)
                .expectRow(2, null, 5);
    }
}
