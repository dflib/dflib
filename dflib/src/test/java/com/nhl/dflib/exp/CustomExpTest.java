package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$int;

public class CustomExpTest {

    @Test
    public void testUnary() {

        Exp<String> exp = $int("b").unary(s -> s.map(i -> "_" + i));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        new SeriesAsserts(exp.eval(df)).expectData("_2", "_4");
    }

    @Test
    public void testUnaryVal() {

        Exp<String> exp = $int("b").unaryVal(i -> "_" + i);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4,
                5, null);

        new SeriesAsserts(exp.eval(df)).expectData("_2", "_4", null);
    }

    @Test
    public void testBinary() {

        Exp<Boolean> exp = $int("b").binary($int("a"), Series::eq);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 3);

        new SeriesAsserts(exp.eval(df)).expectData(false, true);
    }

    @Test
    public void testBinaryVal() {

        Exp<Boolean> exp = $int("b").binaryVal($int("a"), Integer::equals);

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 3,
                5, null);

        new SeriesAsserts(exp.eval(df)).expectData(false, true, null);
    }
}
