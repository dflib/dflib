package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;

public class Window_SelectSortedTest {

    @Test
    public void cols_Implicit() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .sorted($col("b").asc())
                .select(
                        $int("a").sum(),
                        $col("b").first()
                );

        new DataFrameAsserts(r, "sum(a)", "b")
                .expectHeight(5)
                .expectRow(0, 5, "a")
                .expectRow(1, 5, "a")
                .expectRow(2, 5, "a")
                .expectRow(3, 5, "a")
                .expectRow(4, 5, "a");
    }
}
