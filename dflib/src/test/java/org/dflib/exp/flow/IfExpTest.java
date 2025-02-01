package org.dflib.exp.flow;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.exp.ExpBaseTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class IfExpTest extends ExpBaseTest {

    @Test
    public void mix() {
        Exp<String> exp = Exp.ifExp($col("c").eq("x"), $str("a"), $str("b"));

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
        Exp<String> exp = Exp.ifExp($col("c").eq("x"), $str("a"), $str("b"));

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
        Exp<String> exp = Exp.ifExp($col("c").eq("y"), $str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "x",
                null, "5", "x",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "x");

        new SeriesAsserts(exp.eval(df)).expectData("2", "5", "6", null, "9");
    }

    @Test
    public void equalsHashCode() {
        Exp<Integer> e1 = Exp.ifExp($col("a").eq("test"), $int(1), $int(2));
        Exp<Integer> e2 = Exp.ifExp($col("a").eq("test"), $int(1), $int(2));
        Exp<Integer> e3 = Exp.ifExp($col("a").eq("test"), $int(1), $int(2));
        Exp<Integer> different = Exp.ifExp($col("a").eq("test"), $int(1), $int(3));

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different);
    }
}
