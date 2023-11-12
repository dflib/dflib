package com.nhl.dflib.exp.flow;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;

public class IfNullExpTest {

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
}
