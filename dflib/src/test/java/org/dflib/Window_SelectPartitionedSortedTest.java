package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;

public class Window_SelectPartitionedSortedTest {

    @Test
    public void cols_Implicit() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .partitioned("a")
                .sorted($col("b").asc())
                .select(
                        $int("a").sum(),
                        $col("b").first()
                );

        new DataFrameAsserts(r, "sum(a)", "first(b)")
                .expectHeight(5)
                .expectRow(0, 3, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "x")
                .expectRow(3, 0, "a")
                .expectRow(4, 3, "x");
    }
}
