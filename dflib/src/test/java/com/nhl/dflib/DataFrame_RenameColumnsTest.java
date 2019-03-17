package com.nhl.dflib;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DataFrame_RenameColumnsTest {

    @Test
    public void testRenameColumns_All() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
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
        Index i1 = Index.withNames("a", "b");
        DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .renameColumns("c");
    }

    @Test
    public void testRenameColumns_Map() {

        Map<String, String> names = new HashMap<>();
        names.put("b", "c");

        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .renameColumns(names);

        new DFAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }
}
