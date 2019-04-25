package com.nhl.dflib;

import com.nhl.dflib.unit.DoubleSeriesAsserts;
import com.nhl.dflib.unit.IntSeriesAsserts;
import com.nhl.dflib.unit.LongSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class DataFrame_GetColumnTest {

    @Test
    public void testGetColumn_byLabel() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i1,
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn("b");

        new SeriesAsserts(cb).expectData("x", "y");
    }

    @Test
    public void testGetColumn_byPosition() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forSequenceFoldByRow(i1,
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn(0);

        new SeriesAsserts(cb).expectData(1, 2);
    }

    @Test
    public void testGetColumnAsInt_byLabel() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forColumns(i1,
                Series.forData("a", "b"),
                IntSeries.forInts(3, 6, -1));

        IntSeries cb = df.getColumnAsInt("b");

        new IntSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Test
    public void testGetColumnAsInt_byPosition() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forColumns(i1,
                Series.forData("a", "b"),
                IntSeries.forInts(3, 6, -1));

        IntSeries cb = df.getColumnAsInt(1);

        new IntSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetColumnAsInt_NotIntLabel() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forColumns(i1,
                Series.forData("a", "b"),
                IntSeries.forInts(3, 6, -1));

        df.getColumnAsInt("a");
    }


    @Test
    public void testGetColumnAsDouble_byLabel() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forColumns(i1,
                Series.forData("a", "b"),
                DoubleSeries.forDoubles(3., 6.3, -1.01));

        DoubleSeries cb = df.getColumnAsDouble("b");

        new DoubleSeriesAsserts(cb).expectData(3., 6.3, -1.01);
    }

    @Test
    public void testGetColumnAsDouble_byPosition() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forColumns(i1,
                Series.forData("a", "b"),
                DoubleSeries.forDoubles(3., 6.3, -1.01));

        DoubleSeries cb = df.getColumnAsDouble(1);

        new DoubleSeriesAsserts(cb).expectData(3., 6.3, -1.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetColumnAsDouble_NotDoubleLabel() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forColumns(i1,
                Series.forData("a", "b"),
                DoubleSeries.forDoubles(3., 6.3, -1.01));

        df.getColumnAsDouble("a");
    }

    @Test
    public void testGetColumnAsLong_byLabel() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forColumns(i1,
                Series.forData("a", "b"),
                LongSeries.forLongs(3, 6, -1));

        LongSeries cb = df.getColumnAsLong("b");

        new LongSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Test
    public void testGetColumnAsLong_byPosition() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame.forColumns(i1,
                Series.forData("a", "b"),
                LongSeries.forLongs(3, 6, -1));

        LongSeries cb = df.getColumnAsLong(1);

        new LongSeriesAsserts(cb).expectData(3, 6, -1);
    }

}
