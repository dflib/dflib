package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.concat;

public class RowSet_MergeAllTest {

    static final Udf1<Object, String> UDF = e -> concat("[", e, "]").as(e.getColumnName());

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "[1]", "[x]", "[a]")
                .expectRow(1, "[2]", "[y]", "[b]")
                .expectRow(2, "[-1]", "[m]", "[n]");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "[1]", "[x]", "[a]")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, "[-1]", "[m]", "[n]");
    }

    @Test
    public void rowsExcept_byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsExcept(Series.ofInt(0, 2))
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, "[2]", "[y]", "[b]")
                .expectRow(2, -1, "m", "n");
    }

    @Test
    public void byIndex_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2, 2, 0))
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, "[1]", "[x]", "[a]")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, "[-1]", "[m]", "[n]")
                .expectRow(3, "[-1]", "[m]", "[n]")
                .expectRow(4, "[1]", "[x]", "[a]");
    }

    @Test
    public void byRange() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsRange(1, 3)
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1,  "[2]", "[y]", "[b]")
                .expectRow(2, "[-1]", "[m]", "[n]");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "[1]", "[x]", "[a]")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, "[-1]", "[m]", "[n]");
    }
}
