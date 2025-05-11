package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_Select_MappersTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows()
                .select(
                        r -> r.getInt(0) * 3,
                        r -> r.get(1) + "" + r.get(2),
                        r -> r.get(2)
                );

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
                .select(
                        r -> r.getInt(0) * 3,
                        r -> r.get(1) + "" + r.get(2),
                        r -> r.get(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n");
    }

    @Test
    public void byRange() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rowsRange(0, 2)
                .select(
                        r -> r.getInt(0) * 3,
                        r -> r.get(1) + "" + r.get(2),
                        r -> r.get(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b");
    }


    @Test
    public void byCondition() {
        DataFrame df = DataFrame.byColumn("a", "b", "c")
                .of(
                        Series.ofInt(1, 2, -1),
                        Series.of("x", "y", "m"),
                        Series.of("a", "b", "n"))
                .rows(Series.ofBool(true, false, true))
                .select(
                        r -> r.getInt(0) * 3,
                        r -> r.get(1) + "" + r.get(2),
                        r -> r.get(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n");
    }
}
