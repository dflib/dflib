package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Deprecated
public class DataFrame_AddColumnsTest {

    @Test
    public void addColumn() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .addColumn("c", r -> ((int) r.get(0)) * 10);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", 10)
                .expectRow(1, 2, "y", 20);
    }

    @Test
    public void addColumn_DupeName() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .addColumn("a", r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "a", "b", "a_")
                .expectHeight(2)
                .expectRow(0, 1, "x", 10)
                .expectRow(1, 2, "y", 20);
    }

    @Test
    public void addSingleValueColumn() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y").addSingleValueColumn("c", 5);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", 5)
                .expectRow(1, 2, "y", 5);
    }

    @Test
    public void addColumn_Series_DupeName() {

        Series<String> column = Series.of("m", "n");

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y").addColumn("a", column);


        new DataFrameAsserts(df, "a", "b", "a_")
                .expectHeight(2)
                .expectRow(0, 1, "x", "m")
                .expectRow(1, 2, "y", "n");
    }

    @Test
    public void addColumn_Series() {

        Series<String> column = Series.of("m", "n");

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y").addColumn("c", column);


        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "m")
                .expectRow(1, 2, "y", "n");
    }

    @Test
    public void addColumn_Series_Shorter() {

        Series<String> column = Series.of("m");
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        assertThrows(IllegalArgumentException.class, () -> df.addColumn("c", column));
    }

    @Test
    public void addColumn_Series_Longer() {

        Series<String> column = Series.of("m", "n", "o");
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        assertThrows(IllegalArgumentException.class, () -> df.addColumn("c", column));
    }

    @Test
    public void addColumns() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .addColumns(new String[]{"c", "d"},
                        r -> ((int) r.get(0)) * 10,
                        r -> ((int) r.get(0)) * 2);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x", 10, 2)
                .expectRow(1, 2, "y", 20, 4);
    }

    @Test
    public void addColumns_SizeMismatch() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        assertThrows(IllegalArgumentException.class, () -> df.addColumns(new String[]{"c", "d", "e"},
                r -> ((int) r.get(0)) * 10,
                r -> ((int) r.get(0)) * 2));
    }

    @Test
    public void addColumns_RowMapper() {
        DataFrame df = DataFrame.foldByRow("a").of(1, 2)
                .compactInt(0, 0)
                .addColumns(new String[]{"c", "d"}, (f, t) -> t
                        .set(0, f.getInt(0) + 1)
                        .set(1, f.getInt(0) + 2));

        new DataFrameAsserts(df, "a", "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, 2, 3)
                .expectRow(1, 2, 3, 4);
    }

    @Test
    public void addColumn_Exp() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .addColumn($int("a").as("a_copy"))
                .addColumn($col("b").as("b_copy"))
                .addColumn($val("!").as("x"));


        new DataFrameAsserts(df, "a", "b", "a_copy", "b_copy", "x")
                .expectHeight(2)
                .expectRow(0, 1, "x", 1, "x", "!")
                .expectRow(1, 2, "y", 2, "y", "!");
    }

    @Test
    public void addColumn_Exp_Pos() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .addColumn($col(1))
                .addColumn($col(1).eq("x"));

        new DataFrameAsserts(df, "a", "b", "b_", "b='x'")
                .expectHeight(2)
                .expectRow(0, 1, "x", "x", true)
                .expectRow(1, 2, "y", "y", false);
    }

    @Test
    public void addColumns_Exp() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .addColumns(
                        $int("a").as("a_copy"),
                        $col("b").as("b_copy"),
                        $val("!").as("x"));


        new DataFrameAsserts(df, "a", "b", "a_copy", "b_copy", "x")
                .expectHeight(2)
                .expectRow(0, 1, "x", 1, "x", "!")
                .expectRow(1, 2, "y", 2, "y", "!");
    }
}
