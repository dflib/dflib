package com.nhl.dflib.row;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Test;

import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class RowDataFrameTest {

    @Test
    public void testConstructor() {

        Index i = Index.withNames("a");
        DataFrame df = new RowDataFrame(i, asList(
                new Object[]{1},
                new Object[]{2}));

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }


    @Test
    public void testFromStreamFoldByRow0() {

        Index i = Index.withNames("a", "b");
        DataFrame df = RowDataFrame.fromStreamFoldByRow(i, IntStream.range(1, 5).boxed());

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void testFromStreamFoldByRow1() {

        Index i = Index.withNames("a", "b");
        DataFrame df = RowDataFrame.fromStreamFoldByRow(i, IntStream.range(1, 6).boxed());

        new DFAsserts(df, i)
                .expectHeight(3)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, 5, null);
    }

    @Test
    public void testFromSequenceFoldByRow0() {

        Index i = Index.withNames("a", "b");
        DataFrame df = RowDataFrame.fromSequenceFoldByRow(i);

        new DFAsserts(df, i).expectHeight(0);
    }

    @Test
    public void testFromSequenceFoldByRow1() {

        Index i = Index.withNames("a", "b");
        DataFrame df = RowDataFrame.fromSequenceFoldByRow(i, 1, 2);

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testFromSequence2() {

        Index i = Index.withNames("a", "b");
        DataFrame df = RowDataFrame.fromSequenceFoldByRow(i, 1, 2, 3);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Test
    public void testFromSequenceFoldByRow3() {

        Index i = Index.withNames("a", "b");
        DataFrame df = RowDataFrame.fromSequenceFoldByRow(i, 1, 2, 3, 4);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

}
