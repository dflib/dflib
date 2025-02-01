package org.dflib.exp.flow;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.exp.ExpBaseTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class IfNullExpTest extends ExpBaseTest {

    @Test
    public void string() {
        Exp<String> noNulls = ifNull($str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "1", "2",
                null, "5",
                null, "6",
                null, null);

        new SeriesAsserts(noNulls.eval(df)).expectData("1", "5", "6", null);
    }

    @Test
    public void number() {
        Exp<?> noNulls = ifNull($int("a"), $int("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                null, 5,
                8, 6,
                null, 7);

        new SeriesAsserts(noNulls.eval(df)).expectData(1, 5, 8, 7);
    }

    @Test
    public void val() {
        Exp<?> noNulls = ifNull($int("a"), 77);

        DataFrame df = DataFrame.foldByRow("a").of(
                1,
                null,
                8,
                null);

        new SeriesAsserts(noNulls.eval(df)).expectData(1, 77, 8, 77);
    }

    @Test
    public void equalsHashCode() {
        Exp<Integer> e1 = ifNull($int("a"), $int(1));
        Exp<Integer> e2 = ifNull($int("a"), $int(1));
        Exp<Integer> e3 = ifNull($int("a"), $int(1));
        Exp<Integer> different1 = ifNull($int("b"), $int(1));
        Exp<Integer> different2 = ifNull($int("a"), $int(2));

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different1);
        assertNotEquals(e1, different2);
    }
}
