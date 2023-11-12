package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_RenameColumnsTest {

    @Test
    public void renameColumns() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, "x", 2, "y")
                .renameColumns("c", "d");

        new DataFrameAsserts(df, "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void renameColumns_sizeMismatch() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, "x", 2, "y");
        assertThrows(IllegalArgumentException.class, () -> df.renameColumns("c"));
    }

    @Test
    public void renameColumns_withMap() {

        Map<String, String> names = new HashMap<>();
        names.put("b", "c");

        DataFrame df = DataFrame.foldByRow("a", "b").of(1, "x", 2, "y")
                .renameColumns(names);

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void renameColumns_withFunction() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, "x", 2, "y")
                .renameColumns(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void renameColumn() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, "x", 2, "y")
                .renameColumn("b", "d");

        new DataFrameAsserts(df, "a", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void renameColumns_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, "x", 2, "y");
        assertThrows(IllegalArgumentException.class, () -> df.renameColumn("b", "a"));
    }
}
