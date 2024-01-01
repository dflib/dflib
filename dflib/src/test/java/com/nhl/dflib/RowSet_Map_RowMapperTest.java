package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_Map_RowMapperTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows()
                .map((f, t) -> {
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
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(Series.ofInt(0, 2))
                .map((f, t) -> {
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
    public void byIndex_Repeating() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(Series.ofInt(0, 2, 2))
                .map((f, t) -> {
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
    public void byIndex_Unordered() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(Series.ofInt(2, 0))
                .map((f, t) -> {
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
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(Series.ofBool(true, false, true))
                .map((f, t) -> {
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
