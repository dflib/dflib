package com.nhl.dflib;

import org.junit.Test;

import java.util.stream.Stream;

public class DataFrameFactoryTest {

    @Test
    public void testForRowStream0() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forStreamFoldByRow(i, Stream.empty());

        new DFAsserts(df, i).expectHeight(0);
    }

    @Test
    public void testForRowStream1() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forStreamFoldByRow(i, Stream.of(1, 2));

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testForRowStream2() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forStreamFoldByRow(i, Stream.of(1, 2, 3));

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Test
    public void testForRowStream3() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forStreamFoldByRow(i, Stream.of(1, 2, 3, 4));

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }


    @Test
    public void testForRowSequence0() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByRow(i);

        new DFAsserts(df, i).expectHeight(0);
    }

    @Test
    public void testForRowSequence1() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByRow(i, 1, 2);

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testForRowSequence2() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByRow(i, 1, 2, 3);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Test
    public void testForRowSequence3() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByRow(i, 1, 2, 3, 4);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void testForColumnSequence0() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByColumn(i);

        new DFAsserts(df, i).expectHeight(0);
    }

    @Test
    public void testForColumnSequence1() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByColumn(i, 1, 2);

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testForColumnSequence2() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByColumn(i, 1, 2, 3);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 3)
                .expectRow(1, 2, null);
    }

    @Test
    public void testForColumnSequence3() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByColumn(i, 1, 2, 3, 4);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 3)
                .expectRow(1, 2, 4);
    }
}
