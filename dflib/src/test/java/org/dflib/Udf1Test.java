package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Udf1Test {

    @Test
    void byName() {

        Udf1<String, String> udf = e -> e.castAsStr().trim();

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        "xx", "x ",
                        " m", null,
                        null, " y")
                .cols("a", "b").map(udf.call("a"), udf.call("b"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "xx", "x")
                .expectRow(1, "m", null)
                .expectRow(2, null, "y");
    }

    @Test
    void byName_Bool() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, null,
                null, "y");

        Udf1<?, Boolean> udf = e -> e.isNotNull();

        new DataFrameAsserts(df.rows(udf.call("a").castAsBool()).select(), "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, null);

        new DataFrameAsserts(df.rows(udf.call("b").castAsBool()).select(), "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, null, "y");
    }

    @Test
    void byPos() {

        Udf1<String, String> udf = e -> e.castAsStr().trim();

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        "xx", "x ",
                        " m", null,
                        null, " y")
                .cols("a", "b").map(udf.call(0), udf.call(1));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "xx", "x")
                .expectRow(1, "m", null)
                .expectRow(2, null, "y");
    }

    @Test
    void byExp() {

        Udf1<String, String> udf = e -> e.castAsStr().trim();

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        "xx", "x ",
                        " m", null,
                        null, " y")
                .cols("a", "b").map(udf.call($str("a")), udf.call($col("b")));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "xx", "x")
                .expectRow(1, "m", null)
                .expectRow(2, null, "y");
    }
}
