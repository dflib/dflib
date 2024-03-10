package org.dflib.exp.condition;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.bool.OrCondition;
import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrConditionTest {

    @Test
    public void or_Multiple() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                false, false, false,
                true, true, true,
                true, false, false);

        BooleanSeries s = or($bool("a"), $bool("b"), $bool("c")).eval(df);
        new BoolSeriesAsserts(s).expectData(false, true, true);
    }

    @Test
    public void firstMatch_DataFrame() {
        OrCondition c = new OrCondition(Exp.$bool(0), Exp.$bool(1));

        DataFrame df0 = DataFrame.byColumn("t1", "f1")
                .of(Series.ofBool(false, false), Series.ofBool(false, false));
        assertEquals(-1, c.firstMatch(df0));

        DataFrame df1 = DataFrame.byColumn("t1", "f1")
                .of(Series.ofBool(true, true), Series.ofBool(false, false));
        assertEquals(0, c.firstMatch(df1));

        DataFrame df2 = DataFrame.byColumn("t1", "f1")
                .of(Series.ofBool(false, false), Series.ofBool(false, true));
        assertEquals(1, c.firstMatch(df2));
    }

    @Test
    public void firstMatch_Series() {
        OrCondition c = new OrCondition(Exp.$bool(0), Exp.$bool(1));

        Series<Boolean> s0 = Series.ofBool(false, false);
        assertEquals(-1, c.firstMatch(s0));

        Series<Boolean> s1 = Series.ofBool(true, false);
        assertEquals(0, c.firstMatch(s1));

        Series<Boolean> s2 = Series.ofBool(false, true);
        assertEquals(1, c.firstMatch(s2));
    }
}
