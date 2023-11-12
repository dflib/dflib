package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.nhl.dflib.Exp.$decimal;
import static com.nhl.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.*;

public class DataFrame_Sort_Test {

    @Test
    public void sort_Immutable() {
        DataFrame dfi = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                2, 3,
                -1, 2);

        DataFrame df = dfi.sort($int("a").asc());
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
    public void sort_NullsLast() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                null, 3,
                -1, 2).sort($int("a").asc());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, null, 3);
    }

    @Test
    public void sort_ByColumn_NullsLast() {
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

        DataFrame dfd = dfi.sort("a", false);
        new DataFrameAsserts(dfd, "a", "b")
                .expectHeight(3)
                .expectRow(0, null, 3)
                .expectRow(1, 0, 1)
                .expectRow(2, -1, 2);
    }

    @Test
    public void sortByColumns_Names() {
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
    public void sortByColumns_Names_Nulls() {
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
    public void sortByColumns_Positions() {
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
    public void sortByColumn_Position_Direction() {
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

    @Test
    public void sort_NoSorter() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                2, 3,
                -1, 2)
                // a case of missing sorter
                .sort();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 1)
                .expectRow(1, 2, 3)
                .expectRow(2, -1, 2);
    }

    @Test
    public void sort_WithSorter_Asc() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                2, 3,
                -1, 2)
                .sort($int("a").asc());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, 2, 3);
    }

    @Test
    public void sort_WithSorter_Desc() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                2, 3,
                -1, 2)
                .sort($int("a").desc());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 2, 3)
                .expectRow(1, 0, 1)
                .expectRow(2, -1, 2);
    }

    @Test
    public void sort_WithSorter_Multiple() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                0, 1,
                2, 3,
                2, 2,
                -1, 2)
                .sort($int("a").asc(), $int("b").asc());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, 2, 2)
                .expectRow(3, 2, 3);
    }

    @Test
    public void sort_WithSorter_BigDecimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                0, new BigDecimal("2"),
                2, new BigDecimal("1.0"),
                -1, new BigDecimal("-12.05"))
                .sort($decimal("b").asc());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, new BigDecimal("-12.05"))
                .expectRow(1, 2, new BigDecimal("1.0"))
                .expectRow(2, 0, new BigDecimal("2"));
    }
}
