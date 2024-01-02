package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_UniqueRowsTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        "a", 1,
                        "b", 2,
                        "a", 2,
                        "a", 1
                )
                .uniqueRows();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "a", 2);
    }

    @Test
    public void byColumnName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        "a", 1, "A",
                        "b", 2, "B",
                        "a", 2, "C",
                        "a", 1, "D"
                )
                .uniqueRows("a", "b");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "a", 1, "A")
                .expectRow(1, "b", 2, "B")
                .expectRow(2, "a", 2, "C");
    }

    @Test
    public void byColumnPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        "a", 1, "A",
                        "b", 2, "B",
                        "a", 2, "C",
                        "a", 1, "D"
                )
                .uniqueRows(0, 1);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "a", 1, "A")
                .expectRow(1, "b", 2, "B")
                .expectRow(2, "a", 2, "C");
    }

    @Test
    public void byColumnName_OrderOfKeyIsIrrelevant() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        "a", 1, "A",
                        "b", 2, "B",
                        "a", 2, "C",
                        "a", 1, "D"
                )
                .uniqueRows("b", "a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "a", 1, "A")
                .expectRow(1, "b", 2, "B")
                .expectRow(2, "a", 2, "C");
    }
}
