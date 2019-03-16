package com.nhl.dflib.columnar;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Test;

public class ColumnarDataFrameTest {

    @Test
    public void testFromRowSequence0() {

        Index i = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromRowSequence(i);

        new DFAsserts(df, i).expectHeight(0);
    }

    @Test
    public void testFromRowSequence1() {

        Index i = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromRowSequence(i, 1, 2);

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testFromRowSequence2() {

        Index i = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromRowSequence(i, 1, 2, 3);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Test
    public void testFromRowSequence3() {

        Index i = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromRowSequence(i, 1, 2, 3, 4);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void testFromColumnSequence0() {

        Index i = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromColumnSequence(i);

        new DFAsserts(df, i).expectHeight(0);
    }

    @Test
    public void testFromColumnSequence1() {

        Index i = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromColumnSequence(i, 1, 2);

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testFromColumnSequence2() {

        Index i = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromColumnSequence(i, 1, 2, 3);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 3)
                .expectRow(1, 2, null);
    }

    @Test
    public void testFromColumnSequence3() {

        Index i = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromColumnSequence(i, 1, 2, 3, 4);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 3)
                .expectRow(1, 2, 4);
    }

    @Test
    public void testAddColumn() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = ColumnarDataFrame.fromRowSequence(i1,
                1, "x",
                2, "y")
                .addColumn("c", r -> ((int) r.get(0)) * 10);

        new DFAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", 10)
                .expectRow(1, 2, "y", 20);
    }
}
