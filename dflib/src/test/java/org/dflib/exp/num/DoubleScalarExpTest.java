package org.dflib.exp.num;

import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.ExpBaseTest;
import org.dflib.exp.ScalarExp;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleScalarExpTest extends ExpBaseTest {

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
    public void equalsHashCode() {
        ScalarExp<?> e1 = new DoubleScalarExp(1);
        ScalarExp<?> e2 = new DoubleScalarExp(1);
        ScalarExp<?> e3 = new DoubleScalarExp(1);
        ScalarExp<?> different = new DoubleScalarExp(2);

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different);

        // Test instanceof behavior with other class
        ScalarExp<Double> scalarExp = new ScalarExp<>(1d, Double.class);
        assertEquals(e1, scalarExp, "ScalarExp subclass should equal its parent with same values");
        assertEquals(scalarExp, e1, "ScalarExp should equal its subclass instance with same values");
    }
}
