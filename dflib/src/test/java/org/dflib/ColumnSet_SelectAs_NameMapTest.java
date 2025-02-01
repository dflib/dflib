package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ColumnSet_SelectAs_NameMapTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .selectAs(Map.of("a", "c", "b", "d"));

        new DataFrameAsserts(df, "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c")
                .selectAs(Map.of("a", "X", "c", "Y"));

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(2)
                .expectRow(0, 1, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void byName_AddCols_PartialRename() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c", "new")
                .selectAs(Map.of("a", "X", "new", "NEW"));

        new DataFrameAsserts(df, "X", "c", "NEW")
                .expectHeight(2)
                .expectRow(0, 1, "a", null)
                .expectRow(1, 2, "b", null);
    }
}
