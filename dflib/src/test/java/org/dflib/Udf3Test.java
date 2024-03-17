package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Udf3Test {

    @Test
    void byName() {

        Udf3<String, String, String, String> udf = (e1, e2, e3) -> concat(e1, e2, e3);

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        "xx", "X", "_",
                        "m", null, "M",
                        null, "y", "Y")
                .cols("c").select(udf.call("c", "a", "b"));

        new DataFrameAsserts(df, "c")
                .expectHeight(3)
                .expectRow(0, "_xxX")
                .expectRow(1, null)
                .expectRow(2, null);
    }

    @Test
    void byName_Bool() {

        Udf3<?, ?, ?, Boolean> udf = (e1, e2, e3) -> or(e1.isNull(), e2.isNull(), e3.isNull());

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", "X",
                        2, "y", null,
                        2, null, "Z",
                        null, "a", "A")
                .rows(udf.call("a", "b", "c").castAsBool()).select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 2, "y", null)
                .expectRow(1, 2, null, "Z")
                .expectRow(2, null, "a", "A");
    }

    @Test
    void byPos() {

        Udf3<String, String, String, String> udf = (e1, e2, e3) -> concat(e1, e2, e3);

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        "xx", "X", "_",
                        "m", null, "M",
                        null, "y", "Y")
                .cols("c").select(udf.call(2, 0, 1));

        new DataFrameAsserts(df, "c")
                .expectHeight(3)
                .expectRow(0, "_xxX")
                .expectRow(1, null)
                .expectRow(2, null);
    }

    @Test
    void byExp() {

        Udf3<String, String, String, String> udf = (e1, e2, e3) -> concat(e1, e2, e3);

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        "xx", "X", "_",
                        "m", null, "M",
                        null, "y", "Y")
                .cols("c").select(udf.call($col("c"), $col(0), $col("b")));

        new DataFrameAsserts(df, "c")
                .expectHeight(3)
                .expectRow(0, "_xxX")
                .expectRow(1, null)
                .expectRow(2, null);
    }
}
