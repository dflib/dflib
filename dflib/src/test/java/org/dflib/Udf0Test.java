package org.dflib;

import org.dflib.exp.RowNumExp;
import org.dflib.exp.agg.CountExp;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class Udf0Test {

    @Test
    void udf0() {
        Udf0<Integer> udf1 = RowNumExp::getInstance;
        Udf0<Integer> udf2 = CountExp::getInstance;

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        "xx", "x ",
                        " m", null,
                        null, " y")
                .cols("a", "b").merge(udf1.call(), udf2.call());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, 3)
                .expectRow(1, 2, 3)
                .expectRow(2, 3, 3);
    }

}
