package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.bool.OrCondition;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$bool;
import static com.nhl.dflib.Exp.or;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrConditionTest {

    @Test
    public void testOr_Multiple() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                false, false, false,
                true, true, true,
                true, false, false);

        BooleanSeries s = or($bool("a"), $bool("b"), $bool("c")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true, true);
    }

    @Test
    public void testFirstMatch_DataFrame() {
        OrCondition c = new OrCondition(Exp.$bool(0), Exp.$bool(1));

        DataFrame df0 = DataFrame.newFrame("t1", "f1")
                .columns(Series.ofBool(false, false), Series.ofBool(false, false));
        assertEquals(-1, c.firstMatch(df0));

        DataFrame df1 = DataFrame.newFrame("t1", "f1")
                .columns(Series.ofBool(true, true), Series.ofBool(false, false));
        assertEquals(0, c.firstMatch(df1));

        DataFrame df2 = DataFrame.newFrame("t1", "f1")
                .columns(Series.ofBool(false, false), Series.ofBool(false, true));
        assertEquals(1, c.firstMatch(df2));
    }

    @Test
    public void testFirstMatch_Series() {
        OrCondition c = new OrCondition(Exp.$bool(0), Exp.$bool(1));

        Series<Boolean> s0 = Series.ofBool(false, false);
        assertEquals(-1, c.firstMatch(s0));

        Series<Boolean> s1 = Series.ofBool(true, false);
        assertEquals(0, c.firstMatch(s1));

        Series<Boolean> s2 = Series.ofBool(false, true);
        assertEquals(1, c.firstMatch(s2));
    }
}
