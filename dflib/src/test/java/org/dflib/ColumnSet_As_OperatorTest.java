package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_As_OperatorTest {

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .as(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "[b]")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c")
                .as(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "b", "[c]")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }

    @Test
    public void newColumn() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c", "new")
                .as(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "b", "[c]", "[new]")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a", null)
                .expectRow(1, 2, "y", "b", null);
    }
}
