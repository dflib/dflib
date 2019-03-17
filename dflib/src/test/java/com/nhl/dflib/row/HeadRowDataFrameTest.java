package com.nhl.dflib.row;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.RowMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class HeadRowDataFrameTest {

    private Index columns;
    private List<Object[]> rows;

    @Before
    public void initDataFrame() {
        this.columns = Index.withNames("a");
        this.rows = asList(
                DataFrame.row("one"),
                DataFrame.row("two"),
                DataFrame.row("three"),
                DataFrame.row("four"));
    }

    @Test
    public void testToString() {
        HeadRowDataFrame df = new HeadRowDataFrame(DataFrame.fromSequenceFoldByRow(columns, "one", "two", "three"), 2);
        assertEquals("HeadRowDataFrame [{a:one},{a:two}]", df.toString());
    }

    @Test
    public void testConstructor() {

        HeadRowDataFrame df = new HeadRowDataFrame(new IterableRowDataFrame(columns, rows), 3);

        new DFAsserts(df, columns)
                .expectHeight(3)
                .expectRow(0, "one")
                .expectRow(1, "two")
                .expectRow(2, "three");
    }

    @Test
    public void testHead() {

        HeadRowDataFrame df = new HeadRowDataFrame(new IterableRowDataFrame(columns, rows), 3);

        DataFrame df1 = df.head(3);
        assertSame(df, df1);

        DataFrame df2 = df.head(5);
        assertSame(df, df2);

        DataFrame df3 = df.head(2);
        assertNotSame(df, df3);

        new DFAsserts(df3, columns)
                .expectHeight(2)
                .expectRow(0, "one")
                .expectRow(1, "two");
    }

    @Test
    public void testMap() {

        DataFrame df = new HeadRowDataFrame(new IterableRowDataFrame(columns, rows), 3)
                .map(columns, RowMapper.mapColumn("a", r -> r.get(0) + "_"));

        new DFAsserts(df, columns)
                .expectHeight(3)
                .expectRow(0, "one_")
                .expectRow(1, "two_")
                .expectRow(2, "three_");
    }

    @Test
    public void testMap_ChangeRowStructure() {

        Index i1 = Index.withNames("c");

        DataFrame df = new HeadRowDataFrame(new IterableRowDataFrame(columns, rows), 2)
                .map(i1, (s, t) -> t.setValues(s.get(0) + "_"));

        new DFAsserts(df, i1)
                .expectHeight(2)
                .expectRow(0, "one_")
                .expectRow(1, "two_");
    }

    @Test
    public void testMap_ChangeRowStructure_EmptyDF() {

        Index i1 = Index.withNames("c");

        DataFrame df = new HeadRowDataFrame(new IterableRowDataFrame(columns, Collections.emptyList()), 2)
                .map(i1, (s, t) -> t.setValues(s.get(0) + "_"));

        new DFAsserts(df, i1).expectHeight(0);
    }

    @Test
    public void testZip_LeftIsShorter() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = DataFrame.fromListOfRows(i1, asList(
                DataFrame.row(1),
                DataFrame.row(2)));

        Index i2 = Index.withNames("b");
        DataFrame df2 = DataFrame.fromListOfRows(i2, asList(
                DataFrame.row(10),
                DataFrame.row(20)));


        DataFrame df = new HeadRowDataFrame(df1, 1).hConcat(df2);

        new DFAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 10);
    }
}
