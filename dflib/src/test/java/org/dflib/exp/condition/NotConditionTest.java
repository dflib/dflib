package org.dflib.exp.condition;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.not;

public class NotConditionTest {

    @Test
    public void test() {
        BooleanSeries s = Series.ofBool(false, true, true);

        Condition c1 = not($bool(0));
        new BoolSeriesAsserts(c1.eval(s)).expectData(true, false, false);

        Condition c2 = not(c1);
        new BoolSeriesAsserts(c2.eval(s)).expectData(false, true, true);
    }
}
