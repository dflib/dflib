package com.nhl.dflib.exp;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;

public class Boolean_ConditionTest {

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
    public void testNot() {
        BooleanSeries s = BooleanSeries.forBooleans(false, true, true);

        Condition c1 = not($bool("0"));
        new BooleanSeriesAsserts(c1.eval(s)).expectData(true, false, false);

        Condition c2 = not(c1);
        new BooleanSeriesAsserts(c2.eval(s)).expectData(false, true, true);
    }
}
