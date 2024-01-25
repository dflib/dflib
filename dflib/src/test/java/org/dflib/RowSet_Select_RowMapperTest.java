package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_Select_RowMapperTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows()
                .select((f, t) -> {
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
                .cols(0).compactInt(0)
                .rows(Series.ofInt(0, 2))
                .select((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n");
    }

    @Test
    public void byIndex_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows(Series.ofInt(0, 2, 2, 0))
                .select((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n")
                .expectRow(2, -3, "mn", "n")
                .expectRow(3, 3, "xa", "a");
    }

    @Test
    public void byIndex_Empty() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows(Series.ofInt())
                .select((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c").expectHeight(0);
    }

    @Test
    public void byIndex_ExpandWithDupes() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows(0, 2, 2, 0)
                .select((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n")
                .expectRow(2, -3, "mn", "n")
                .expectRow(3, 3, "xa", "a");
    }

    @Test
    public void byRange() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rowsRange(1, 2)
                .select((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(1)
                .expectRow(0, 6, "yb", "b");
    }

    @Test
    public void byRange_Empty() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rowsRange(1, 1)
                .select((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c").expectHeight(0);
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows(Series.ofBool(true, false, true))
                .select((f, t) -> {
                    t.set(0, f.getInt(0) * 3);
                    t.set(1, f.get(1) + "" + f.get(2));
                    t.set(2, f.get(2));
                });

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n");
    }
}
