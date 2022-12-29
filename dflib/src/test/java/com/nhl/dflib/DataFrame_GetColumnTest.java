package com.nhl.dflib;

import com.nhl.dflib.unit.DoubleSeriesAsserts;
import com.nhl.dflib.unit.IntSeriesAsserts;
import com.nhl.dflib.unit.LongSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_GetColumnTest {

    @Test
    public void testGetColumn_byLabel() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn("b");

        new SeriesAsserts(cb).expectData("x", "y");
    }

    @Test
    public void testGetColumn_byPosition() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn(0);

        new SeriesAsserts(cb).expectData(1, 2);
    }

    @Test
    public void testGetColumnAsInt_byLabel() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofInt(3, 6, -1));

        IntSeries cb = df.getColumnAsInt("b");

        new IntSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Test
    public void testGetColumnAsInt_byPosition() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .columns(Series.of("a", "b", "x"), Series.ofInt(3, 6, -1));

        IntSeries cb = df.getColumnAsInt(1);

        new IntSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Test
    public void testGetColumnAsInt_NotIntLabel() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .columns(Series.of("a", "b", "x"), Series.ofInt(3, 6, -1));

        assertThrows(IllegalArgumentException.class, () -> df.getColumnAsInt("a"));
    }


    @Test
    public void testGetColumnAsDouble_byLabel() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .columns(Series.of("a", "b", "x"), Series.ofDouble(3., 6.3, -1.01));

        DoubleSeries cb = df.getColumnAsDouble("b");

        new DoubleSeriesAsserts(cb).expectData(3., 6.3, -1.01);
    }

    @Test
    public void testGetColumnAsDouble_byPosition() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .columns(Series.of("a", "b", "x"), Series.ofDouble(3., 6.3, -1.01));

        DoubleSeries cb = df.getColumnAsDouble(1);

        new DoubleSeriesAsserts(cb).expectData(3., 6.3, -1.01);
    }

    @Test
    public void testGetColumnAsDouble_NotDoubleLabel() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .columns(Series.of("a", "b", "x"), Series.ofDouble(3., 6.3, -1.01));

        assertThrows(IllegalArgumentException.class, () -> df.getColumnAsDouble("a"));
    }

    @Test
    public void testGetColumnAsLong_byLabel() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .columns(Series.of("a", "b", "x"), Series.ofLong(3, 6, -1));

        LongSeries cb = df.getColumnAsLong("b");

        new LongSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Test
    public void testGetColumnAsLong_byPosition() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .columns(Series.of("a", "b", "x"), Series.ofLong(3, 6, -1));

        LongSeries cb = df.getColumnAsLong(1);

        new LongSeriesAsserts(cb).expectData(3, 6, -1);
    }

}
