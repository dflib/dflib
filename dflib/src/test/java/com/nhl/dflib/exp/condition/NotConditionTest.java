package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$bool;
import static com.nhl.dflib.Exp.not;

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
