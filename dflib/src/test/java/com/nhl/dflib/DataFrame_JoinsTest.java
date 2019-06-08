package com.nhl.dflib;

import com.nhl.dflib.join.JoinIndicator;
import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

import java.util.Objects;

public class DataFrame_JoinsTest {

    @Deprecated
    @Test
    public void testNestedLoop_Inner_Legacy() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2, JoinPredicate.on(0, 0));

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testNestedLoop_Inner() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin()
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testNestedLoop_Inner_NoMatches() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin()
                .predicatedBy((lr, rr) -> false)
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(0);
    }

    @Test
    public void testNestedLoop_Inner_IndexOverlap() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "b").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin()
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .with(df2);

        new DFAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testNestedLoop_Left() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1
                .leftJoin()
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b");
    }

    @Test
    public void testNestedLoop_Right() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2
                .rightJoin()
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .with(df1);

        new DFAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testNestedLoop_Full() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1
                .fullJoin()
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }

    @Test
    public void testNestedLoop_Indicator() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.fullJoin()
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .indicatorColumn("ind")
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d", "ind")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null, JoinIndicator.left_only)
                .expectRow(1, 2, "y", 2, "a", JoinIndicator.both)
                .expectRow(2, 2, "y", 2, "b", JoinIndicator.both)
                .expectRow(3, null, null, 3, "c", JoinIndicator.right_only);
    }

    @Test
    public void testHash_Inner() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin()
                .on(Hasher.forColumn(0), Hasher.forColumn(1))
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 2, "y", "a", 2)
                .expectRow(1, 2, "y", "b", 2)
                .expectRow(2, 4, "z", "x", 4);
    }

    @Test
    public void testHash_Full_IntColumn() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .toIntColumn(0, 0);

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c").toIntColumn(0, 0);

        DataFrame df = df1.fullJoin()
                .on(0)
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }


    @Test
    public void testHash_Inner_Indexed_HashOverlap() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "b").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin()
                .on(0)
                .with(df2);

        new DFAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testHash_Left() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.leftJoin()
                .on(0)
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b");
    }

    @Test
    public void testHash_Right_ByPos() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.rightJoin()
                .on(0)
                .with(df1);

        new DFAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testHash_Right_ByName() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.rightJoin()
                .on("c", "a")
                .with(df1);

        new DFAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testHash_Right_ByMatchingName() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.rightJoin()
                .on("a")
                .with(df1);

        new DFAsserts(df, "a", "d", "a_", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testHash_Full() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.fullJoin()
                .on(0)
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }

    @Test
    public void testHash_MultiColumnHash() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "a",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.innerJoin()
                .on("c", "a")
                .on("d", "b")
                .with(df1);

        new DFAsserts(df, "c", "d", "a", "b")
                .expectHeight(1)
                .expectRow(0, 2, "a", 2, "a");
    }

    @Test
    public void testHash_Indicator() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("c", "d").foldByRow(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.fullJoin()
                .on(0)
                .indicatorColumn("ind")
                .with(df2);

        new DFAsserts(df, "a", "b", "c", "d", "ind")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null, JoinIndicator.left_only)
                .expectRow(1, 2, "y", 2, "a", JoinIndicator.both)
                .expectRow(2, 2, "y", 2, "b", JoinIndicator.both)
                .expectRow(3, null, null, 3, "c", JoinIndicator.right_only);
    }
}
