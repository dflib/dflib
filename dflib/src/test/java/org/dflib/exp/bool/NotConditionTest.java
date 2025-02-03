package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.Series;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class NotConditionTest extends BaseExpTest {

    @Test
    public void test() {
        BooleanSeries s = Series.ofBool(false, true, true);

        Condition c1 = not($bool(0));
        new BoolSeriesAsserts(c1.eval(s)).expectData(true, false, false);

        Condition c2 = not(c1);
        new BoolSeriesAsserts(c2.eval(s)).expectData(false, true, true);
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                not($col("a").eq("test")),
                not($col("a").eq("test")),
                not($col("b").eq("test")));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                not($col("a").eq("test")),
                not($col("a").eq("test")),
                not($col("b").eq("test")));
    }
}
