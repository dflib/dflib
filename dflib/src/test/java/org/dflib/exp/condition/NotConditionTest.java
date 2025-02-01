package org.dflib.exp.condition;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.ExpBaseTest;
import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NotConditionTest extends ExpBaseTest {

    @Test
    public void test() {
        BooleanSeries s = Series.ofBool(false, true, true);

        Condition c1 = not($bool(0));
        new BoolSeriesAsserts(c1.eval(s)).expectData(true, false, false);

        Condition c2 = not(c1);
        new BoolSeriesAsserts(c2.eval(s)).expectData(false, true, true);
    }

    @Test
    public void equalsHashCode() {
        Exp<Boolean> e1 = not($col("a").eq("test"));
        Exp<Boolean> e2 = not($col("a").eq("test"));
        Exp<Boolean> e3 = not($col("a").eq("test"));
        Exp<Boolean> different = not($col("b").eq("test"));

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different);
    }
}
