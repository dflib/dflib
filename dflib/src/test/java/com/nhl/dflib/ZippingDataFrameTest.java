package com.nhl.dflib;

import com.nhl.dflib.zip.Zipper;
import org.junit.Test;

import static java.util.Arrays.asList;

public class ZippingDataFrameTest {

    @Test
    public void testConstructor() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = new SimpleDataFrame(i1, asList(
                DataRow.row(1),
                DataRow.row(2)));

        Index i2 = Index.withNames("b");
        DataFrame df2 = new SimpleDataFrame(i2, asList(
                DataRow.row(10),
                DataRow.row(20)));

        DataFrame df = new ZippingDataFrame(Index.withNames("a", "b"), df1, df2, Zipper.rowZipper());

        new DFAsserts(df, "a", "b")
                .assertLength(2)
                .assertRow(0, 1, 10)
                .assertRow(1, 2, 20);
    }

    @Test
    public void testConstructor_SparseDF() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = new SimpleDataFrame(i1, asList(
                DataRow.row(0, 1),
                DataRow.row(2, 3))).selectColumns("b");

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = new SimpleDataFrame(i2, asList(
                DataRow.row(10, 20),
                DataRow.row(30, 40))).selectColumns("c");

        DataFrame df = new ZippingDataFrame(
                Index.withNames("z1", "z2"),
                df1,
                df2,
                Zipper.rowZipper());

        new DFAsserts(df, "z1", "z2")
                .assertLength(2)
                .assertRow(0, 1, 10)
                .assertRow(1, 3, 30);
    }

    @Test
    public void testConstructor_SparseDF_ZippedIndex() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = new SimpleDataFrame(i1, asList(
                DataRow.row(0, 1),
                DataRow.row(2, 3))).selectColumns("b");

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = new SimpleDataFrame(i2, asList(
                DataRow.row(10, 20),
                DataRow.row(30, 40))).selectColumns("c");

        DataFrame df = new ZippingDataFrame(
                Zipper.zipIndex(df1.getColumns(), df2.getColumns()),
                df1,
                df2,
                Zipper.rowZipper());

        new DFAsserts(df, "b", "c")
                .assertLength(2)
                .assertRow(0, 1, 10)
                .assertRow(1, 3, 30);
    }

    @Test
    public void testHead() {
        Index i1 = Index.withNames("a");
        DataFrame df1 = new SimpleDataFrame(i1, asList(
                DataRow.row(1),
                DataRow.row(2)));

        Index i2 = Index.withNames("b");
        DataFrame df2 = new SimpleDataFrame(i2, asList(
                DataRow.row(10),
                DataRow.row(20)));

        DataFrame df = new ZippingDataFrame(Index.withNames("a", "b"), df1, df2, Zipper.rowZipper())
                .head(1);

        new DFAsserts(df, "a", "b")
                .assertLength(1)
                .assertRow(0, 1, 10);
    }

    @Test
    public void testRenameColumn() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = new SimpleDataFrame(i1, asList(
                DataRow.row(1),
                DataRow.row(2)));

        Index i2 = Index.withNames("b");
        DataFrame df2 = new SimpleDataFrame(i2, asList(
                DataRow.row(10),
                DataRow.row(20)));

        DataFrame df = new ZippingDataFrame(Index.withNames("a", "b"), df1, df2, Zipper.rowZipper())
                .renameColumn("b", "c");

        new DFAsserts(df, "a", "c")
                .assertLength(2)
                .assertRow(0, 1, 10)
                .assertRow(1, 2, 20);
    }

    @Test
    public void testMap() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = new SimpleDataFrame(i1, asList(
                DataRow.row("one"),
                DataRow.row("two")));

        Index i2 = Index.withNames("b");
        DataFrame df2 = new SimpleDataFrame(i2, asList(
                DataRow.row(1),
                DataRow.row(2)));

        Index zippedColumns = Index.withNames("x", "y");

        DataFrame df = new ZippingDataFrame(zippedColumns, df1, df2, Zipper.rowZipper())
                .map((c, r) -> c.mapColumn(r, "x", (cx, rx) -> cx.get(rx, 0) + "_"));

        new DFAsserts(df, zippedColumns)
                .assertLength(2)
                .assertRow(0, "one_", 1)
                .assertRow(1, "two_", 2);
    }

    @Test
    public void testMap_ChangeRowStructure() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = new SimpleDataFrame(i1, asList(
                DataRow.row("one"),
                DataRow.row("two")));

        Index i2 = Index.withNames("b");
        DataFrame df2 = new SimpleDataFrame(i2, asList(
                DataRow.row(1),
                DataRow.row(2)));

        Index mappedColumns = Index.withNames("x", "y", "z");
        DataFrame df = new ZippingDataFrame(Index.withNames("a", "b"), df1, df2, Zipper.rowZipper())
                .map(mappedColumns, (c, r) -> c.target(
                        r[0],
                        ((int) r[1]) * 10,
                        r[1]));

        new DFAsserts(df, mappedColumns)
                .assertLength(2)
                .assertRow(0, "one", 10, 1)
                .assertRow(1, "two", 20, 2);
    }
}
