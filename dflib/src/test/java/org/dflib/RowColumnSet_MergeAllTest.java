package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.concat;

public class RowColumnSet_MergeAllTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b", "c")
            .of(
                    1, "x", "a",
                    2, "y", "b",
                    -1, "m", "n");

    static final Udf1<Object, String> UDF = e -> concat("[", e, "]").as(e.getColumnName());

    @Test
    public void all() {

        DataFrame df = TEST_DF
                .rows()
                .cols()
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "[1]", "[x]", "[a]")
                .expectRow(1, "[2]", "[y]", "[b]")
                .expectRow(2, "[-1]", "[m]", "[n]");
    }

    @Test
    public void allRows_colsByName() {

        DataFrame df = TEST_DF
                .rows()
                .cols("b", "a")
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "[1]", "[x]", "a")
                .expectRow(1, "[2]", "[y]", "b")
                .expectRow(2, "[-1]", "[m]", "n");
    }

    @Test
    public void rowsByIndex_colsByName() {

        DataFrame df = TEST_DF
                .rows(Series.ofInt(0, 2))
                .cols("b", "a")
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "[1]", "[x]", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, "[-1]", "[m]", "n");
    }

    @Test
    public void rowsByIndex_colsByName_AddRows_AddCols() {

        DataFrame df = TEST_DF
                .rows(0, 2, 2)
                .cols("b", "a", "x")
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c", "x")
                .expectHeight(4)
                .expectRow(0, "[1]", "[x]", "a", null)
                .expectRow(1, 2, "y", "b", null)
                .expectRow(2, "[-1]", "[m]", "n", null)
                .expectRow(3, "[-1]", "[m]", "n", null);
    }

    @Test
    public void rowsByCondition_colsByName() {
        DataFrame df = TEST_DF
                .rows(Series.ofBool(true, false, true))
                .cols("b", "a")
                .mergeAll(UDF);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "[1]", "[x]", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, "[-1]", "[m]", "n");
    }
}
