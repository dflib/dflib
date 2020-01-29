package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataFrame_Sort_Test {

    @Test
    public void testSort_Immutable() {
        DataFrame dfi = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                2, 3,
                -1, 2);

        DataFrame df = dfi.sort(r -> (Integer) r.get("a"));
        assertNotSame(dfi, df);

        new DataFrameAsserts(dfi, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 1)
                .expectRow(1, 2, 3)
                .expectRow(2, -1, 2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, 2, 3);

    }

    @Test
    public void testSort_WithKeyExtractor() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                2, 3,
                -1, 2)
                .sort(r -> (Integer) r.get("a"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, 2, 3);
    }

    @Test
    public void testSort_NullsLast() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                null, 3,
                -1, 2).sort(r -> (Integer) r.get("a"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, null, 3);
    }

    @Test
    public void testSort_ByColumn_NullsLast() {
        DataFrame dfi = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                null, 3,
                -1, 2);

        DataFrame dfa = dfi.sort("a", true);
        new DataFrameAsserts(dfa, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, null, 3);

        // nulls must be last regardless of ascending or descending order...
        DataFrame dfd = dfi.sort("a", false);
        new DataFrameAsserts(dfd, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 1)
                .expectRow(1, -1, 2)
                .expectRow(2, null, 3);
    }

    @Test
    public void testSortByColumns_Names() {
        DataFrame dfi = DataFrame.newFrame("a", "b").foldByRow(
                0, 4,
                2, 2,
                0, 2);

        DataFrame dfab = dfi.sort(new String[]{"a", "b"}, new boolean[]{true, true});
        new DataFrameAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 4)
                .expectRow(2, 2, 2);

        DataFrame dfba = dfi.sort(new String[]{"b", "a"}, new boolean[]{true, true});
        new DataFrameAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 2, 2)
                .expectRow(2, 0, 4);
    }

    @Test
    public void testSortByColumns_Names_Nulls() {
        DataFrame dfi = DataFrame.newFrame("a", "b").foldByRow(
                0, 4,
                2, null,
                0, 2);

        DataFrame dfab = dfi.sort(new String[]{"a", "b"}, new boolean[]{true, true});
        new DataFrameAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 4)
                .expectRow(2, 2, null);

        DataFrame dfba = dfi.sort(new String[]{"b", "a"}, new boolean[]{true, true});
        new DataFrameAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 4)
                .expectRow(2, 2, null);
    }

    @Test
    public void testSortByColumns_Positions() {
        DataFrame dfi = DataFrame.newFrame("a", "b").foldByRow(
                0, 4,
                2, 2,
                0, 2);

        DataFrame dfab = dfi.sort(new int[]{0, 1}, new boolean[]{true, true});
        new DataFrameAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 4)
                .expectRow(2, 2, 2);

        DataFrame dfba = dfi.sort(new int[]{1, 0}, new boolean[]{true, true});
        new DataFrameAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 2, 2)
                .expectRow(2, 0, 4);

    }

    @Test
    public void testSortByColumn_Position_Direction() {
        DataFrame dfi = DataFrame.newFrame("a", "b").foldByRow(
                0, 3,
                2, 4,
                0, 2);

        DataFrame dfab = dfi.sort(1, false);
        new DataFrameAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 2, 4)
                .expectRow(1, 0, 3)
                .expectRow(2, 0, 2);

        DataFrame dfba = dfi.sort(1, true);
        new DataFrameAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 3)
                .expectRow(2, 2, 4);
    }
}
