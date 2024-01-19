package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_SelectRename_OperatorTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .selectRename(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "[b]")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a",
                        2, "y", "b")
                .cols("a", "c")
                .selectRename(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "[c]")
                .expectHeight(2)
                .expectRow(0, 1, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void newColumn() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a",
                        2, "y", "b")
                .cols("a", "c", "new")
                .selectRename(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "[c]", "[new]")
                .expectHeight(2)
                .expectRow(0, 1, "a", null)
                .expectRow(1, 2, "b", null);
    }
}
