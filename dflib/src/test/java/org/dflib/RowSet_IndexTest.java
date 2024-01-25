package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_IndexTest {

    @Test
    public void all() {
        IntSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .index();

        new IntSeriesAsserts(index).expectData(0, 1, 2);
    }

    @Test
    public void byIndex() {
        IntSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .index();

        new IntSeriesAsserts(index).expectData(0, 2);
    }

    @Test
    public void byIndex_Duplicate() {
        IntSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows(Series.ofInt(0, 2, 2, 0))
                .index();

        // duplicates and ordering of RowSet is preserved
        new IntSeriesAsserts(index).expectData(0, 2, 2, 0);
    }

    @Test
    public void byRange() {
        IntSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsRange(1, 3)
                .index();

        new IntSeriesAsserts(index).expectData(1, 2);
    }

    @Test
    public void byCondition() {
        IntSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .index();

        new IntSeriesAsserts(index).expectData(0, 2);
    }
}
