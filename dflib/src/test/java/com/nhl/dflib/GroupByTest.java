package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class GroupByTest {

    @Test
    public void testGroup() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        GroupBy gb = df.group(Hasher.forColumn("a"));
        assertNotNull(gb);

        assertEquals(3, gb.size());
        assertEquals(new HashSet<>(asList(0, 1, 2)), new HashSet<>(gb.getGroups()));

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
    public void testGroup_Empty() {
        DataFrame df = DataFrame.newFrame("a", "b").empty();

        GroupBy gb = df.group(Hasher.forColumn("a"));
        assertNotNull(gb);

        assertEquals(0, gb.size());
        assertEquals(Collections.emptySet(), new HashSet<>(gb.getGroups()));
    }

    @Test
    public void testGroup_NullKeysIgnored() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                null, "a",
                1, "x");

        GroupBy gb = df.group(Hasher.forColumn("a"));
        assertNotNull(gb);

        assertEquals(2, gb.size());
        assertEquals(new HashSet<>(asList(1, 2)), new HashSet<>(gb.getGroups()));

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
    public void testGroup_Agg() {
        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame df = df1.group("a").agg(Aggregator.sumLong("a"), Aggregator.concat("b", ";"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }

    @Test
    public void testGroup_Agg_MultipleAggregationsForKey() {
        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df = df1
                .group("b")
                .agg(Aggregator.first("b"), Aggregator.sumLong("a"), Aggregator.medianDouble("a"));

        new DataFrameAsserts(df, "b", "a", "a_")
                .expectHeight(3)
                .expectRow(0, "x", 2L, 1.)
                .expectRow(1, "y", 3L, 1.5)
                .expectRow(2, "a", 0L, 0.);
    }

    @Test
    public void testGroup_toDataFrame() {
        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testGroup_Head_toDataFrame() {
        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testGroup_Head_Sort_toDataFrame() {
        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testGroup_Tail_toDataFrame() {
        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testGroup_Agg_Named() {
        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df = df1.group("b").agg(
                Aggregator.first("b"),
                Aggregator.sumLong("a").named("a_sum"),
                Aggregator.medianDouble("a").named("a_median"));

        new DataFrameAsserts(df, "b", "a_sum", "a_median")
                .expectHeight(3)
                .expectRow(0, "x", 2L, 1.)
                .expectRow(1, "y", 3L, 1.5)
                .expectRow(2, "a", 0L, 0.);
    }
}
