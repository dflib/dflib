package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.dflib.Exp.*;

public class GroupByTest {

    @Test
    public void group() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        GroupBy gb = df.group(Hasher.of("a"));
        assertNotNull(gb);

        assertEquals(3, gb.size());
        assertEquals(new HashSet<>(asList(0, 1, 2)), new HashSet<>(gb.getGroupKeys()));

        new DataFrameAsserts(gb.getGroup(0), "a", "b")
                .expectHeight(1)
                .expectRow(0, 0, "a");

        new DataFrameAsserts(gb.getGroup(1), "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "z")
                .expectRow(2, 1, "x");

        new DataFrameAsserts(gb.getGroup(2), "a", "b")
                .expectHeight(1)
                .expectRow(0, 2, "y");
    }

    @Test
    public void group_Empty() {
        DataFrame df = DataFrame.empty("a", "b");

        GroupBy gb = df.group(Hasher.of("a"));
        assertNotNull(gb);

        assertEquals(0, gb.size());
        assertEquals(Collections.emptySet(), new HashSet<>(gb.getGroupKeys()));
    }

    @Test
    public void group_NullKeysIgnored() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                null, "a",
                1, "x");

        GroupBy gb = df.group(Hasher.of("a"));
        assertNotNull(gb);

        assertEquals(2, gb.size());
        assertEquals(new HashSet<>(asList(1, 2)), new HashSet<>(gb.getGroupKeys()));

        new DataFrameAsserts(gb.getGroup(1), "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "z")
                .expectRow(2, 1, "x");

        new DataFrameAsserts(gb.getGroup(2), "a", "b")
                .expectHeight(1)
                .expectRow(0, 2, "y");
    }

    @Test
    public void group_Agg() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame df = df1.group("a").agg(
                $long("a").sum(),
                $str(1).vConcat(";"));

        new DataFrameAsserts(df, "sum(a)", "b")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }

    @Test
    public void group_Agg_MultipleAggregationsForKey() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df = df1
                .group("b")
                .agg(
                        $col("b").first(),
                        $long("a").sum(),
                        $double("a").median());

        new DataFrameAsserts(df, "b", "sum(a)", "median(a)")
                .expectHeight(3)
                .expectRow(0, "x", 2L, 1.)
                .expectRow(1, "y", 3L, 1.5)
                .expectRow(2, "a", 0L, 0.);
    }

    @Test
    public void group_toDataFrame() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df = df1.group("a").toDataFrame();

        // must be sorted by groups in the order they are encountered
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 1, "x")
                .expectRow(3, 2, "y")
                .expectRow(4, 0, "a");
    }

    @Test
    public void group_Head_toDataFrame() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df2 = df1.group("a")
                .head(2)
                .toDataFrame();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 2, "y")
                .expectRow(3, 0, "a");

        DataFrame df3 = df1.group("a")
                .head(1)
                .toDataFrame();

        new DataFrameAsserts(df3, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 0, "a");
    }

    @Test
    public void group_Head_Sort_toDataFrame() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df2 = df1.group("a")
                .sort("b", false)
                .head(2)
                .toDataFrame();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "y")
                .expectRow(1, 1, "x")
                .expectRow(2, 2, "y")
                .expectRow(3, 0, "a");
    }

    @Test
    public void group_Tail_toDataFrame() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "z");

        DataFrame df2 = df1.group("a")
                .tail(2)
                .toDataFrame();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "y")
                .expectRow(1, 1, "z")
                .expectRow(2, 2, "y")
                .expectRow(3, 0, "a");

        DataFrame df3 = df1.group("a")
                .tail(1)
                .toDataFrame();

        new DataFrameAsserts(df3, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "z")
                .expectRow(1, 2, "y")
                .expectRow(2, 0, "a");
    }

    @Test
    public void group_Agg_Named() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df = df1.group("b").agg(
                $col("b").first().as("first"),
                $col("b").last().as("last"),
                $long("a").sum().as("a_sum"),
                $double("a").median().as("a_median")
        );

        new DataFrameAsserts(df, "first", "last", "a_sum", "a_median")
                .expectHeight(3)
                .expectRow(0, "x", "x", 2L, 1.)
                .expectRow(1, "y", "y", 3L, 1.5)
                .expectRow(2, "a", "a", 0L, 0.);
    }

    @Test
    public void group_Sort_Pos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "b",
                2, "a",
                1, "z",
                0, "n",
                1, "y");

        DataFrame df2 = df1.group("a")
                .sort(1, true)
                .toDataFrame();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 1, "z")
                .expectRow(3, 2, "a")
                .expectRow(4, 2, "b")
                .expectRow(5, 0, "n");
    }

    @Test
    public void group_Sort_Name() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "b",
                2, "a",
                1, "z",
                0, "n",
                1, "y");

        DataFrame df2 = df1.group("a")
                .sort("b", true)
                .toDataFrame();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 1, "z")
                .expectRow(3, 2, "a")
                .expectRow(4, 2, "b")
                .expectRow(5, 0, "n");
    }

    @Test
    public void group_Sort_Names() {
        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", 2,
                2, "b", 1,
                2, "a", 2,
                1, "z", -1,
                0, "n", 5,
                1, "x", 1);

        DataFrame df2 = df1.group("a")
                .sort(new String[]{"b", "c"}, new boolean[]{true, true})
                .toDataFrame();

        new DataFrameAsserts(df2, "a", "b", "c")
                .expectHeight(6)
                .expectRow(0, 1, "x", 1)
                .expectRow(1, 1, "x", 2)
                .expectRow(2, 1, "z", -1)
                .expectRow(3, 2, "a", 2)
                .expectRow(4, 2, "b", 1)
                .expectRow(5, 0, "n", 5);
    }

    @Test
    public void rank_NoSort() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn1 = df.group("a").rank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 1, 1, 1);

        IntSeries rn2 = df.group("b").rank();
        new IntSeriesAsserts(rn2).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void sort_Rank() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn1 = df.group("a").sort("b", true).rank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 3, 1, 1);

        IntSeries rn2 = df.group("b").sort("a", true).rank();
        new IntSeriesAsserts(rn2).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void group_Sort_Sorters() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "b",
                2, "a",
                1, "z",
                0, "n",
                1, "y");

        DataFrame df2 = df1.group("a")
                .sort($col(1).asc())
                .toDataFrame();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 1, "z")
                .expectRow(3, 2, "a")
                .expectRow(4, 2, "b")
                .expectRow(5, 0, "n");
    }

    @Test
    public void sort_Sorter_Rank() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn1 = df.group("a").sort($col("b").asc()).rank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 3, 1, 1);

        IntSeries rn2 = df.group("b").sort($col("a").asc()).rank();
        new IntSeriesAsserts(rn2).expectData(1, 1, 1, 1, 1);
    }

}
