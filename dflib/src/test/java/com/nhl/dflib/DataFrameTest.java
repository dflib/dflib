package com.nhl.dflib;

import com.nhl.dflib.map.RowMapper;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class DataFrameTest {

    @Test
    public void testFromObjects() {

        class Bean {
            int a;
            int b;

            Bean(int a, int b) {
                this.a = a;
                this.b = b;
            }
        }

        List<Bean> beans = asList(new Bean(5, 4), new Bean(3, 1));

        Index i = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromObjects(i, beans, b -> DataFrame.row(b.a, b.b));

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 5, 4)
                .expectRow(1, 3, 1);
    }

    @Test
    public void testFromStream0() {

        Index i = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromStreamFoldByRow(i, IntStream.range(1, 5).boxed());

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void testFromStream1() {

        Index i = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromStreamFoldByRow(i, IntStream.range(1, 6).boxed());

        new DFAsserts(df, i)
                .expectHeight(3)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, 5, null);
    }

    @Test
    public void testFromSequence0() {

        Index i = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i);

        new DFAsserts(df, i).expectHeight(0);
    }

    @Test
    public void testFromSequence1() {

        Index i = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i, 1, 2);

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testFromSequence2() {

        Index i = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i, 1, 2, 3);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Test
    public void testFromSequence3() {

        Index i = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i, 1, 2, 3, 4);

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void testFromListOfRows() {

        Index i = Index.withNames("a");
        DataFrame df = DataFrame.fromListOfRows(i, asList(
                new Object[]{1},
                new Object[]{2}));

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }


    @Test
    public void testMap_SameIndex() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .map(RowMapper.mapColumn("a", r -> ((int) r.get("a")) * 10));

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testMap_SameIndex_Sparse() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .dropColumns("a")
                .map(RowMapper.mapColumn("b", r -> r.get("b") + "_"));

        new DFAsserts(df, new IndexPosition(0, 0, "b"))
                .expectHeight(2)
                .expectRow(0, "x_")
                .expectRow(1, "y_");
    }

    @Test
    public void testMapColumn_FromRow() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .mapColumn("a", r -> ((int) r.get(0)) * 10);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testMapColumn_FromRow_Sparse() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .selectColumns("b")
                .mapColumn("b", r -> r.get(0) + "_");

        new DFAsserts(df, "b")
                .expectHeight(2)
                .expectRow(0, "x_")
                .expectRow(1, "y_");
    }

    @Test
    public void testMapColumn_FromValue() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .mapColumnValue("a", v -> ((int) v) * 10);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testFilterByColumn_Name() {

        Index i1 = Index.withNames("a");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1, 10, 20)
                .filterByColumn("a", (Integer v) -> v > 15);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testFilterByColumn_Pos() {

        Index i1 = Index.withNames("a");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1, 10, 20)
                .filterByColumn(0, (Integer v) -> v > 15);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }
}
