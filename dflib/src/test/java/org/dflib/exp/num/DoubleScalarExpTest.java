package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleScalarExpTest extends BaseExpTest {

    @Test
    public void eval() {
        NumExp<Double> exp = new DoubleScalarExp(5.5);
        Series<Double> s = exp.eval(Series.of(1, 2, 3));
        new SeriesAsserts(s).expectData(5.5, 5.5, 5.5);
    }

    @Test
    public void toQL() {
        NumExp<Double> exp = new DoubleScalarExp(5.5);
        assertEquals("5.5", exp.toQL());
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                new DoubleScalarExp(1),
                new DoubleScalarExp(1),
                new DoubleScalarExp(2));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                new DoubleScalarExp(1),
                new DoubleScalarExp(1),
                new DoubleScalarExp(2));
    }
}
