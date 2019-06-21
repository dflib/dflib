package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class DataFrame_FactoryMethodsTest {

    @Test
    public void testNewFrame_Strings() {

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(1, 2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Deprecated
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
        DataFrame df = DataFrame.forObjects(i, beans, b -> new Object[]{b.a, b.b});

        new DataFrameAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 5, 4)
                .expectRow(1, 3, 1);
    }

    @Deprecated
    @Test
    public void testForStream0() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forStreamFoldByRow(i, IntStream.range(1, 5).boxed());

        new DataFrameAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Deprecated
    @Test
    public void testForStream1() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forStreamFoldByRow(i, IntStream.range(1, 6).boxed());

        new DataFrameAsserts(df, i)
                .expectHeight(3)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, 5, null);
    }

    @Deprecated
    @Test
    public void testForSequence0() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i);

        new DataFrameAsserts(df, i).expectHeight(0);
    }

    @Deprecated
    @Test
    public void testForSequence1() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i, 1, 2);

        new DataFrameAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Deprecated
    @Test
    public void testForSequence2() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i, 1, 2, 3);

        new DataFrameAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Deprecated
    @Test
    public void testForSequence3() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i, 1, 2, 3, 4);

        new DataFrameAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Deprecated
    @Test
    public void testForSequenceFoldByColumn() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByColumn(i, 1, 2, 3, 4);

        new DataFrameAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 3)
                .expectRow(1, 2, 4);
    }

    @Deprecated
    @Test
    public void testForSequenceFoldByColumn_Partial() {

        Index i = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByColumn(i, 1, 2, 3, 4, 5);

        new DataFrameAsserts(df, i)
                .expectHeight(3)
                .expectRow(0, 1, 4)
                .expectRow(1, 2, 5)
                .expectRow(2, 3, null);
    }

    @Deprecated
    @Test
    public void testForRows() {

        Index i = Index.forLabels("a");
        DataFrame df = DataFrame.forRows(i, asList(
                new Object[]{1},
                new Object[]{2}));

        new DataFrameAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }
}
