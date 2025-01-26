package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_SelectTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b", "c")
            .of(
                    1, "x", "a",
                    2, "y", "b"
            );

    @Test
    public void all() {
        DataFrame df = TEST_DF.cols().select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }

    @Test
    public void byName() {
        DataFrame df = TEST_DF.cols("a", "Y", "c").select();

        new DataFrameAsserts(df, "a", "Y", "c")
                .expectHeight(2)
                .expectRow(0, 1, null, "a")
                .expectRow(1, 2, null, "b");
    }

    @Test
    public void byPos() {
        DataFrame df = TEST_DF.cols(0, 2, 4).select();

        new DataFrameAsserts(df, "a", "c", "4")
                .expectHeight(2)
                .expectRow(0, 1, "a", null)
                .expectRow(1, 2, "b", null);
    }
}
