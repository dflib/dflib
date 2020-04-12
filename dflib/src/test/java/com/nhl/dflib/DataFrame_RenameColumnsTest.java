package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_RenameColumnsTest {

    @Test
    public void testRenameColumns_All() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .renameColumns("c", "d");

        new DataFrameAsserts(df, "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testRenameColumns_SizeMismatch() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        assertThrows(IllegalArgumentException.class, () -> df.renameColumns("c"));
    }

    @Test
    public void testRenameColumns_Map() {

        Map<String, String> names = new HashMap<>();
        names.put("b", "c");

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .renameColumns(names);

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testRenameColumns_WithFunction() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .renameColumns(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testRenameColumn() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .renameColumn("b", "d");

        new DataFrameAsserts(df, "a", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testRenameColumn_Duplicate() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        assertThrows(IllegalArgumentException.class, () -> df.renameColumn("b", "a"));
    }
}
