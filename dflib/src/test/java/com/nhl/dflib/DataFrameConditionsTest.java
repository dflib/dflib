package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrameConditionsTest {

    @Test
    public void eq1() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame eq = df1.eq(df2);

        new DataFrameAsserts(eq, "a", "b")
                .expectHeight(2)
                .expectRow(0, true, true)
                .expectRow(1, true, true);
    }

    @Test
    public void eq2() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                1, "X",
                2, "y");

        DataFrame eq = df1.eq(df2);

        new DataFrameAsserts(eq, "a", "b")
                .expectHeight(2)
                .expectRow(0, true, false)
                .expectRow(1, true, true);
    }

    @Test
    public void ne1() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame eq = df1.ne(df2);

        new DataFrameAsserts(eq, "a", "b")
                .expectHeight(2)
                .expectRow(0, false, false)
                .expectRow(1, false, false);
    }

    @Test
    public void ne2() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "Y");

        DataFrame eq = df1.ne(df2);

        new DataFrameAsserts(eq, "a", "b")
                .expectHeight(2)
                .expectRow(0, false, false)
                .expectRow(1, false, true);
    }

    @Test
    public void ne_ColMismatch() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "B").of(
                1, "x",
                2, "Y");

        assertThrows(IllegalArgumentException.class, () -> df1.ne(df2));
    }

    @Test
    public void ne_RowsMismatch() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "Y");

        assertThrows(IllegalArgumentException.class, () -> df1.ne(df2));
    }
}
