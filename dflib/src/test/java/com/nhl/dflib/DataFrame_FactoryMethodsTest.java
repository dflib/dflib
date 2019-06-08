package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class DataFrame_FactoryMethodsTest {

    @Test
    public void testForObjects() {

        class Bean {
            int a;
            int b;

            Bean(int a, int b) {
                this.a = a;
                this.b = b;
            }
        }

        List<Bean> beans = asList(new Bean(5, 4), new Bean(3, 1));

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forObjects(i, beans, b -> DataFrame.row(b.a, b.b));

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 5, 4)
                .expectRow(1, 3, 1);
    }

    @Test
    public void testForStream0() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forStreamFoldByRow(i, IntStream.range(1, 5).boxed());

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void testForStream1() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forStreamFoldByRow(i, IntStream.range(1, 6).boxed());

        new DFAsserts(df, i)
                .expectHeight(3)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, 5, null);
    }

    @Test
    public void testForSequence0() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i);

        new DFAsserts(df, i).expectHeight(0);
    }

    @Test
    public void testForSequence1() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i, 1, 2);

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testForSequence2() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i, 1, 2, 3);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Test
    public void testForSequence3() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i, 1, 2, 3, 4);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void testForSequenceFoldByColumn() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByColumn(i, 1, 2, 3, 4);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 3)
                .expectRow(1, 2, 4);
    }

    @Test
    public void testForSequenceFoldByColumn_Partial() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByColumn(i, 1, 2, 3, 4, 5);

        new DFAsserts(df, i)
                .expectHeight(3)
                .expectRow(0, 1, 4)
                .expectRow(1, 2, 5)
                .expectRow(2, 3, null);
    }

    @Test
    public void testForRows() {

        Index i = Index.forLabels("a");
        DataFrame df = DataFrame.forRows(i, asList(
                new Object[]{1},
                new Object[]{2}));

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }
}
