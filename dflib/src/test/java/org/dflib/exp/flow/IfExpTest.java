package org.dflib.exp.flow;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class IfExpTest {

    @Test
    public void mix() {
        Exp<String> exp = ifExp($col("c").eq("x"), $str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "x",
                null, "5", "y",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "z");

        new SeriesAsserts(exp.eval(df)).expectData("1", "5", null, "7", "9");
    }

    @Test
    public void allTrue() {
        Exp<String> exp = ifExp($col("c").eq("x"), $str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "x",
                null, "5", "x",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "x");

        new SeriesAsserts(exp.eval(df)).expectData("1", null, null, "7", "8");
    }

    @Test
    public void allFalse() {
        Exp<String> exp = ifExp($col("c").eq("y"), $str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "x",
                null, "5", "x",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "x");

        new SeriesAsserts(exp.eval(df)).expectData("2", "5", "6", null, "9");
    }
}
