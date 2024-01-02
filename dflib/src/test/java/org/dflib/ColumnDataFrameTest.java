package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnDataFrameTest {

    @Deprecated
    @Test
    public void constructor_NoName() {
        ColumnDataFrame df = new ColumnDataFrame(
                Index.of("a", "b"),
                Series.ofInt(1, 2),
                Series.ofInt(3, 4));

        new DataFrameAsserts(df, "a", "b").expectHeight(2);
    }

    @Test
    public void constructor() {
        ColumnDataFrame df = new ColumnDataFrame(
                "n1",
                Index.of("a", "b"),
                Series.ofInt(1, 2),
                Series.ofInt(3, 4));

        new DataFrameAsserts(df, "a", "b").expectHeight(2);
        assertEquals("n1", df.getName());
    }

    @Test
    public void constructor_NoData() {
        ColumnDataFrame df = new ColumnDataFrame(null, Index.of("a", "b"));
        new DataFrameAsserts(df, "a", "b").expectHeight(0);
    }

    @Test
    public void as() {
        DataFrame df = new ColumnDataFrame(
                "n1",
                Index.of("a", "b"),
                Series.ofInt(1, 2),
                Series.ofInt(3, 4)).as("n2");

        new DataFrameAsserts(df, "a", "b").expectHeight(2);
        assertEquals("n2", df.getName());
    }
}
