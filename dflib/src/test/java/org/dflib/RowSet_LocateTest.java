package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_LocateTest {

    @Test
    public void rows_emptyAll() {
        BooleanSeries index = DataFrame.empty("a", "b", "c")
                .rows().locate();

        new BoolSeriesAsserts(index).expectData();
    }

    @Test
    public void all() {
        BooleanSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .locate();

        new BoolSeriesAsserts(index).expectData(true, true, true);
    }

    @Test
    public void byIndex() {
        BooleanSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .locate();

        new BoolSeriesAsserts(index).expectData(true, false, true);
    }

    @Test
    public void byIndex_Duplicate() {
        BooleanSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofInt(0, 2, 2, 0))
                .locate();

        new BoolSeriesAsserts(index).expectData(true, false, true);
    }

    @Test
    public void byRange() {
        BooleanSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsRange(1, 3)
                .locate();

        new BoolSeriesAsserts(index).expectData(false, true, true);
    }

    @Test
    public void byCondition() {
        BooleanSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .locate();

        new BoolSeriesAsserts(index).expectData(true, false, true);
    }

    @Test
    public void rows_byCondition_NoMatches() {
        BooleanSeries index = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(false, false, false))
                .locate();

        new BoolSeriesAsserts(index).expectData(false, false, false);
    }

    @Test
    public void byConditionExp() {
        BooleanSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(r -> Math.abs(r.getInt(0)) == 1).locate();

        new BoolSeriesAsserts(index).expectData(true, false, true);
    }

    @Test
    public void byConditionExp_NoMatches() {
        BooleanSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(r -> false).locate();

        new BoolSeriesAsserts(index).expectData(false, false, false);
    }

}
