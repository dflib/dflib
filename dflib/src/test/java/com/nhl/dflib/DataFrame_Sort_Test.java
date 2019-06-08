package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataFrame_Sort_Test {

    @Test
    public void testSort() {
        DataFrame dfi = DataFrame.builder("a", "b").foldByRow(
                0, 1,
                2, 3,
                -1, 2);

        DataFrame df = dfi.sort(r -> (Integer) r.get("a"));
        assertNotSame(dfi, df);

        new DFAsserts(dfi, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 1)
                .expectRow(1, 2, 3)
                .expectRow(2, -1, 2);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, 2, 3);

    }

    @Test
    public void testSortByColumns_Names() {
        DataFrame dfi = DataFrame.builder("a", "b").foldByRow(
                0, 4,
                2, 2,
                0, 2);

        DataFrame dfab = dfi.sort(new String[]{"a", "b"}, new boolean[]{true, true});
        assertNotSame(dfi, dfab);

        new DFAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 4)
                .expectRow(2, 2, 2);

        DataFrame dfba = dfi.sort(new String[]{"b", "a"}, new boolean[]{true, true});
        assertNotSame(dfi, dfba);

        new DFAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 2, 2)
                .expectRow(2, 0, 4);

    }

    @Test
    public void testSortByColumns_Positions() {
        DataFrame dfi = DataFrame.builder("a", "b").foldByRow(
                0, 4,
                2, 2,
                0, 2);

        DataFrame dfab = dfi.sort(new int[]{0, 1}, new boolean[]{true, true});
        assertNotSame(dfi, dfab);

        new DFAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 4)
                .expectRow(2, 2, 2);

        DataFrame dfba = dfi.sort(new int[]{1, 0}, new boolean[]{true, true});
        assertNotSame(dfi, dfba);

        new DFAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 2, 2)
                .expectRow(2, 0, 4);

    }

    @Test
    public void testSortByColumn_Position_Direction() {
        DataFrame dfi = DataFrame.builder("a", "b").foldByRow(
                0, 3,
                2, 4,
                0, 2);

        DataFrame dfab = dfi.sort(1, false);
        assertNotSame(dfi, dfab);

        new DFAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 2, 4)
                .expectRow(1, 0, 3)
                .expectRow(2, 0, 2);

        DataFrame dfba = dfi.sort(1, true);
        assertNotSame(dfi, dfba);

        new DFAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 3)
                .expectRow(2, 2, 4);
    }
}
