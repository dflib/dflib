package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Udf2Test {

    @Test
    void byName() {

        Udf2<String, String, String> udf = (e1, e2) -> concat(e1, e2);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        "xx", "X",
                        "m", null,
                        null, "y")
                .cols("c").select(udf.call("a", "b"));

        new DataFrameAsserts(df, "c")
                .expectHeight(3)
                .expectRow(0, "xxX")
                .expectRow(1, null)
                .expectRow(2, null);
    }

    @Test
    void byName_Bool() {

        Udf2<?, ?, Boolean> udf = (e1, e2) -> or(e1.isNull(), e2.isNull());

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, null,
                null, "y")
                .rows(udf.call("a", "b").castAsBool()).select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 2, null)
                .expectRow(1, null, "y");
    }

    @Test
    void byPos() {

        Udf2<String, String, String> udf = (e1, e2) -> concat(e1, e2);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        "xx", "X",
                        "m", null,
                        null, "y")
                .cols("c").select(udf.call(0, 1));

        new DataFrameAsserts(df, "c")
                .expectHeight(3)
                .expectRow(0, "xxX")
                .expectRow(1, null)
                .expectRow(2, null);
    }

    @Test
    void byExp() {

        Udf2<String, String, String> udf = (e1, e2) -> concat(e1, e2);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        "xx", "X",
                        "m", null,
                        null, "y")
                .cols("c").select(udf.call($col("a"), $col(1)));

        new DataFrameAsserts(df, "c")
                .expectHeight(3)
                .expectRow(0, "xxX")
                .expectRow(1, null)
                .expectRow(2, null);
    }
}
