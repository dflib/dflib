package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_IndexTest {

    @Test
    public void all() {
        IntSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows()
                .index();

        new IntSeriesAsserts(index).expectData(0, 1, 2);
    }

    @Test
    public void byIndex() {
        IntSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofInt(0, 2))
                .index();

        new IntSeriesAsserts(index).expectData(0, 2);
    }

    @Test
    public void byIndex_Duplicate() {
        IntSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofInt(0, 2, 2, 0))
                .index();

        // duplicates and ordering of RowSet is preserved
        new IntSeriesAsserts(index).expectData(0, 2, 2, 0);
    }

    @Test
    public void byRange() {
        IntSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rowsRange(1, 3)
                .index();

        new IntSeriesAsserts(index).expectData(1, 2);
    }

    @Test
    public void byCondition() {
        IntSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofBool(true, false, true))
                .index();

        new IntSeriesAsserts(index).expectData(0, 2);
    }

    @Test
    public void byConditionExp() {
        IntSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(r -> Math.abs(r.getInt(0)) == 1).index();

        new IntSeriesAsserts(index).expectData(0, 2);
    }

    @Test
    public void byConditionExp_NoMatches() {
        IntSeries index = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(r -> false).index();

        new IntSeriesAsserts(index).expectData();
    }

}
