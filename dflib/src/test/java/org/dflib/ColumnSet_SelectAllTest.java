package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.concat;

public class ColumnSet_SelectAllTest {

    @Test
    public void all() {
        Udf1<Object, String> udf = e -> concat("[", e, "]").as(e.getColumnName() + "_");
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().selectAll(udf);

        new DataFrameAsserts(df, "a_", "b_")
                .expectHeight(2)
                .expectRow(0, "[1]", "[x]")
                .expectRow(1, "[2]", "[y]");
    }

    @Test
    public void allSameNames() {
        Udf1<Object, String> udf = e -> concat("[", e, "]").as(e.getColumnName());
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().selectAll(udf);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "[1]", "[x]")
                .expectRow(1, "[2]", "[y]");
    }

    @Test
    public void byName() {
        Udf1<Object, String> udf = e -> concat("[", e, "]").as(e.getColumnName());

        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").selectAll(udf);

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, "[x]", null)
                .expectRow(1, "[y]", null);
    }

    @Test
    public void byName_Duplicate() {

        Udf1<Object, String> udf = e -> concat("[", e, "]").as(e.getColumnName());

        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "b").selectAll(udf);

        new DataFrameAsserts(df, "b", "b_")
                .expectHeight(2)
                .expectRow(0, "[x]", "[x]")
                .expectRow(1, "[y]", "[y]");
    }

    @Test
    public void byPos() {
        Udf1<Object, String> udf = e -> concat("[", e, "]").as(e.getColumnName());

        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2).selectAll(udf);

        new DataFrameAsserts(df, "b", "2")
                .expectHeight(2)
                .expectRow(0, "[x]", null)
                .expectRow(1, "[y]", null);
    }

    @Test
    public void append() {
        Udf1<Object, String> udf = e -> concat("[", e, "]").as(e.getColumnName());

        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsAppend("b", "c", "b").selectAll(udf);

        new DataFrameAsserts(df, "b_", "c", "b__")
                .expectHeight(2)
                .expectRow(0, null, null, null)
                .expectRow(1, null, null, null);
    }

    @Test
    public void except_ByName() {
        Udf1<Object, String> udf = e -> concat("[", e, "]").as(e.getColumnName());

        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").selectAll(udf);

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, "[x]", "[z]")
                .expectRow(1, "[y]", "[a]");
    }

    @Test
    public void except_ByPos() {
        Udf1<Object, String> udf = e -> concat("[", e, "]").as(e.getColumnName());

        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).selectAll(udf);

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, "[1]", "[z]")
                .expectRow(1, "[2]", "[a]");
    }
}
