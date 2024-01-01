package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_Mappers_ExpTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows()
                .map(
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
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(Series.ofInt(0, 2))
                .map(
                        r -> r.getInt(0) * 3,
                        r -> r.get(1) + "" + r.get(2),
                        r -> r.get(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(Series.ofBool(true, false, true))
                .map(
                        r -> r.getInt(0) * 3,
                        r -> r.get(1) + "" + r.get(2),
                        r -> r.get(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
