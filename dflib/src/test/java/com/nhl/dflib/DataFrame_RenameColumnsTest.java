package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DataFrame_RenameColumnsTest {

    @Test
    public void testRenameColumns_All() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .renameColumns("c", "d");

        new DFAsserts(df, "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRenameColumns_SizeMismatch() {
        DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .renameColumns("c");
    }

    @Test
    public void testRenameColumns_Map() {

        Map<String, String> names = new HashMap<>();
        names.put("b", "c");

        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .renameColumns(names);

        new DFAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testRenameColumns_WithFunction() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .renameColumns(String::toUpperCase);

        new DFAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }
}
