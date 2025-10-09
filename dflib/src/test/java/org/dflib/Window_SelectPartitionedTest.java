package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Window_SelectPartitionedTest {

    static final DataFrame TEST_DF =  DataFrame.foldByRow("a", "b").of(
            1, "x",
            2, "y",
            1, "z",
            0, "a",
            1, "x");

    @Test
    public void implicit() {

        DataFrame r = TEST_DF.over().partition("a").select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)")
                .expectHeight(5)
                .expectRow(0, 3L)
                .expectRow(1, 2L)
                .expectRow(2, 3L)
                .expectRow(3, 0L)
                .expectRow(4, 3L);
    }

    @Test
    public void byName_MultiExp() {

        DataFrame r = TEST_DF.over()
                .partition("a")
                .cols("a", "rn")
                .select(
                        $col("a"),
                        rowNum()
                );

        new DataFrameAsserts(r, "a", "rn")
                .expectHeight(5)
                .expectRow(0, 1, 1)
                .expectRow(1, 2, 1)
                .expectRow(2, 1, 2)
                .expectRow(3, 0, 1)
                .expectRow(4, 1, 3);
    }
}
