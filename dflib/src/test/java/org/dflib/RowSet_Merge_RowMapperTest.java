package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_Merge_RowMapperTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows()
                .merge((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofInt(0, 2))
                .merge((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void byIndex_Duplicate() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofInt(0, 2, 2, 0))
                .merge((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n")
                .expectRow(3, -3, "mn", "n")
                .expectRow(4, 3, "xa", "a");
    }

    @Test
    public void byRange() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rowsRange(1, 2)
                .merge((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -1, "m", "n");
    }

    @Test
    public void byIndex_Unordered() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofInt(2, 0))
                .merge((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofBool(true, false, true))
                .merge((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
