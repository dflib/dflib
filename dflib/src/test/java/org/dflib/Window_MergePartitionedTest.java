package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Window_MergePartitionedTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b").of(
            1, "x",
            2, "y",
            1, "z",
            0, "a",
            1, "x");

    @Test
    public void implicit() {

        DataFrame r = TEST_DF.over().partitioned("a").merge($int("a").sum());
        new DataFrameAsserts(r, "a", "b", "sum(a)")
                .expectHeight(5)
                .expectRow(0, 1, "x", 3)
                .expectRow(1, 2, "y", 2)
                .expectRow(2, 1, "z", 3)
                .expectRow(3, 0, "a", 0)
                .expectRow(4, 1, "x", 3);
    }

    @Test
    public void byName_MultiExp() {

        DataFrame r = TEST_DF.over()
                .partitioned("a")
                .cols("a", "rn", "s")
                .merge(
                        $col("a"),
                        rowNum(),
                        $int("a").sum()
                );

        new DataFrameAsserts(r, "a", "b", "rn", "s")
                .expectHeight(5)
                .expectRow(0, 1, "x", 1, 3)
                .expectRow(1, 2, "y", 1, 2)
                .expectRow(2, 1, "z", 2, 3)
                .expectRow(3, 0, "a", 1, 0)
                .expectRow(4, 1, "x", 3, 3);
    }
}
