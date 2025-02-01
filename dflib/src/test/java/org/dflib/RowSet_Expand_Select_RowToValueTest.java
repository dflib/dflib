package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RowSet_Expand_Select_RowToValueTest {
    
    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, List.of("x1", "x2"), "a", // <--
                        2, List.of("y1", "y2"), "b",
                        4, List.of("e1", "e2"), "k",
                        0, List.of("f1", "f2"), "g", // <--
                        1, List.of("m1", "m2"), "n", // <--
                        5, null, "x") // <--
                .rows(Series.ofBool(true, false, false, true, true, true))
                .expand("b")
                .select(
                        f -> f.get(0, Integer.class) + 1,
                        f -> f.get(1) != null ? f.get(1).toString().toUpperCase() : null,
                        f -> f.get(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(7)
                .expectRow(0, 2, "X1", "a")
                .expectRow(1, 2, "X2", "a")
                .expectRow(2, 1, "F1", "g")
                .expectRow(3, 1, "F2", "g")
                .expectRow(4, 2, "M1", "n")
                .expectRow(5, 2, "M2", "n")
                .expectRow(6, 6, null, "x");
    }
}
