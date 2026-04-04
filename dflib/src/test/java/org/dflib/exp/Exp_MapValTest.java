package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;

public class Exp_MapValTest {

    @Test
    void mapVal_Binary() {
        Exp<String> e = $col(0).mapVal($col(1), (a, b) -> a + "_" + b);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "X", "Y",
                "A", null);

        new SeriesAsserts(e.eval(df)).expectData("X_Y", null);
    }

    @Test
    void mapVal_Binary_Nulls() {
        Exp<String> e = $col(0).mapVal(
                $col(1),
                (a, b) -> (a != null ? a : "?") + "_" + (b != null ? b : "?"),
                false);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "X", "Y",
                "A", null,
                null, "B");

        new SeriesAsserts(e.eval(df)).expectData("X_Y", "A_?", "?_B");
    }
}
