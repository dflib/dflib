package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowColumnSet_SelectTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b", "c")
            .of(
                    1, "x", "a",
                    2, "y", "b",
                    -1, "m", "n");

    @Test
    public void all() {
        DataFrame df = TEST_DF
                .rows()
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(3)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 2)
                .expectRow(2, "m", -1);
    }

    @Test
    public void byIndex() {
        DataFrame df = TEST_DF
                .rows(Series.ofInt(0, 2))
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "m", -1);
    }

    @Test
    public void byIndex_AddRows_AddCols() {
        DataFrame df = TEST_DF
                .rows(0, 2, 2)
                .cols("b", "a", "x")
                .select();

        new DataFrameAsserts(df, "b", "a", "x")
                .expectHeight(3)
                .expectRow(0, "x", 1, null)
                .expectRow(1, "m", -1, null)
                .expectRow(2, "m", -1, null);
    }

    @Test
    public void byCondition() {
        DataFrame df = TEST_DF
                .rows(Series.ofBool(true, false, true))
                .cols("b", "a")
                .select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "m", -1);
    }
}
