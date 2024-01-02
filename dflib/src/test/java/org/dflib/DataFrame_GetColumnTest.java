package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.dflib.unit.IntSeriesAsserts;
import org.dflib.unit.LongSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_GetColumnTest {

    @Test
    public void byLabel() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn("b");

        new SeriesAsserts(cb).expectData("x", "y");
    }

    @Test
    public void byPosition() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn(0);

        new SeriesAsserts(cb).expectData(1, 2);
    }

    @Deprecated
    @Test
    public void asInt_byLabel() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofInt(3, 6, -1));

        IntSeries cb = df.getColumnAsInt("b");

        new IntSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Deprecated
    @Test
    public void asInt_byPosition() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofInt(3, 6, -1));

        IntSeries cb = df.getColumnAsInt(1);

        new IntSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Deprecated
    @Test
    public void asInt_NotIntLabel() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofInt(3, 6, -1));

        assertThrows(IllegalArgumentException.class, () -> df.getColumnAsInt("a"));
    }


    @Deprecated
    @Test
    public void asDouble_byLabel() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofDouble(3., 6.3, -1.01));

        DoubleSeries cb = df.getColumnAsDouble("b");

        new DoubleSeriesAsserts(cb).expectData(3., 6.3, -1.01);
    }

    @Deprecated
    @Test
    public void asDouble_byPosition() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofDouble(3., 6.3, -1.01));

        DoubleSeries cb = df.getColumnAsDouble(1);

        new DoubleSeriesAsserts(cb).expectData(3., 6.3, -1.01);
    }

    @Deprecated
    @Test
    public void asDouble_NotDoubleLabel() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofDouble(3., 6.3, -1.01));

        assertThrows(IllegalArgumentException.class, () -> df.getColumnAsDouble("a"));
    }

    @Deprecated
    @Test
    public void asLong_byLabel() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofLong(3, 6, -1));

        LongSeries cb = df.getColumnAsLong("b");

        new LongSeriesAsserts(cb).expectData(3, 6, -1);
    }

    @Deprecated
    @Test
    public void asLong_byPosition() {
        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "x"), Series.ofLong(3, 6, -1));

        LongSeries cb = df.getColumnAsLong(1);

        new LongSeriesAsserts(cb).expectData(3, 6, -1);
    }
}
