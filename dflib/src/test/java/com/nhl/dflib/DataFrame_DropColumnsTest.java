package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_DropColumnsTest {

    @Test
    public void dropColumns1() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .dropColumns("a");

        new DataFrameAsserts(df, "b")
                .expectHeight(2)
                .expectRow(0, "x")
                .expectRow(1, "y");
    }

    @Test
    public void dropColumns2() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .dropColumns("b");

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }

    @Test
    public void dropColumns3() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .dropColumns();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void dropColumns4() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .dropColumns("no_such_column");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void dropColumns_Predicate() {
        DataFrame df = DataFrame.foldByColumn("a1", "b2", "c1")
                .of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .dropColumns(c -> c.endsWith("1"));

        new DataFrameAsserts(df, "b2")
                .expectHeight(3)
                .expectRow(0, 4)
                .expectRow(1, 5)
                .expectRow(2, 6);
    }
}
