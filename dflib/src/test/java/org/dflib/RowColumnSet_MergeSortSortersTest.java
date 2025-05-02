package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;

public class RowColumnSet_MergeSortSortersTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b", "c")
            .of(
                    1, "x", "a",
                    2, "y", "b",
                    -1, "m", "n");

    @Test
    public void all() {
        DataFrame df = TEST_DF
                .rows().sort($col("a").asc())
                .cols()
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, -1, "m", "n")
                .expectRow(1, 1, "x", "a")
                .expectRow(2, 2, "y", "b");
    }
}
