package com.nhl.dflib;

import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_JoinsTest extends BaseDataFrameTest {

    @Test
    public void testInnerJoin() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
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
    public void testInnerJoin_NoMatches() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2, (lr, rr) -> false);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(0);
    }

    @Test
    public void testInnerJoin_IndexOverlap() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("a", "b");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2, JoinPredicate.on(0, 0));

        new DFAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_Left() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.join(df2, JoinPredicate.on(0, 0), JoinType.left);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_Right() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.join(df1, JoinPredicate.on(0, 0), JoinType.right);

        new DFAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testJoin_Full() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.join(df2, JoinPredicate.on(0, 0), JoinType.full);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }

    @Test
    public void testHashJoin_Full_IntColumn() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y")
                .toIntColumn(0, 0);

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c").toIntColumn(0, 0);

        DataFrame df = df1.join(df2, 0, 0, JoinType.full);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }

    @Test
    public void testInnerJoin_Hash() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y",
                4, "z");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2, Hasher.forColumn(0), Hasher.forColumn(1));

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 2, "y", "a", 2)
                .expectRow(1, 2, "y", "b", 2)
                .expectRow(2, 4, "z", "x", 4);
    }

    @Test
    public void testInnerJoin_Indexed_HashOverlap() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("a", "b");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2, Hasher.forColumn(0), Hasher.forColumn(0));

        new DFAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_LeftHash() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.join(df2, Hasher.forColumn(0), Hasher.forColumn(0), JoinType.left);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_RightHash() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.join(df1, Hasher.forColumn(0), Hasher.forColumn(0), JoinType.right);

        new DFAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testJoin_RightHash_ByPos() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.join(df1, 0, 0, JoinType.right);

        new DFAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testJoin_RightHash_ByName() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.join(df1, "c", "a", JoinType.right);

        new DFAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testJoin_RightHash_ByMatchingName() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("a", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.join(df1, "a", JoinType.right);

        new DFAsserts(df, "a", "d", "a_", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testJoin_FullHash() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = createDf(i2,
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.join(df2, Hasher.forColumn(0), Hasher.forColumn(0), JoinType.full);

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }
}
