package com.nhl.dflib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class DataFrame_HeadTest extends BaseDataFrameTest {

    public DataFrame_HeadTest(boolean columnar) {
        super(columnar);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{{false}, {true}});
    }

    @Test
    public void testHead() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y",
                3, "z")
                .head(2);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testHead_Zero() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y",
                3, "z")
                .head(0);

        new DFAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void testHead_OutOfBounds() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y",
                3, "z")
                .head(4);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z");
    }
}
