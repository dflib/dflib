package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

/**
 * @deprecated Zero-based "GroupBy.rowNumbers" is deprecated
 */
@Deprecated
public class GroupBy_WindowFuncs_DeprecatedTest {

    @Test
    public void testGroupBy_RowNumbers_Emtpy() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").empty();
        Series<Integer> rn = df.group("a").rowNumbers();
        new SeriesAsserts(rn).expectData();
    }

    @Test
    public void testGroupBy_RowNumbers0() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        Series<Integer> rn = df.group("a").rowNumbers();
        new SeriesAsserts(rn).expectData(0, 0, 1, 0, 2);
    }

    @Test
    public void testGroupBy_RowNumbers1() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                3, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                -1, "x", "s");

        Series<Integer> rn = df.group("a").rowNumbers();
        new SeriesAsserts(rn).expectData(0, 0, 0, 0, 0);
    }

    @Test
    public void testGroupBy_RowNumbers2() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                3, "x", "m",
                0, "y", "n",
                3, "z", "k",
                3, "a", "f",
                1, "x", "s");

        Series<Integer> rn = df.group("a").rowNumbers();
        new SeriesAsserts(rn).expectData(0, 0, 1, 2, 0);
    }

    @Test
    public void testGroupBy_RowNumbers_Sort() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                3, "x", "m",
                0, "y", "n",
                3, "z", "k",
                3, "a", "f",
                1, "x", "s");

        Series<Integer> rn = df.group("a").sort("b", true).rowNumbers();
        new SeriesAsserts(rn).expectData(1, 0, 2, 0, 0);
    }
}
