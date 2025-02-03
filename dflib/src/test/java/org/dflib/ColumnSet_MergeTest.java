package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_MergeTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b", "c")
            .of(
                    1, "x", "a",
                    2, "y", "b"
            );

    @Test
    public void all() {
        DataFrame df = TEST_DF.cols().merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }

    @Test
    public void byName_AddCols() {
        DataFrame df = TEST_DF.cols("a", "Y", "c").merge();

        new DataFrameAsserts(df, "a", "b", "c", "Y")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a", null)
                .expectRow(1, 2, "y", "b", null);
    }

    @Test
    public void byPos() {
        DataFrame df = TEST_DF.cols(0, 2, 4).merge();

        new DataFrameAsserts(df, "a", "b", "c", "4")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a", null)
                .expectRow(1, 2, "y", "b", null);
    }
}
