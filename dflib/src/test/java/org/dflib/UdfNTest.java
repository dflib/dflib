package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.*;

public class UdfNTest {

    @Test
    void byName() {

        UdfN<String> udf = exps -> concat(exps);

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

        UdfN<Boolean> udf = exps ->
                or(List.of(exps).stream().map(Exp::isNull).toArray(Condition[]::new));

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

        UdfN<String> udf = exps -> concat(exps);

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

        UdfN<String> udf = exps -> concat(exps);

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
