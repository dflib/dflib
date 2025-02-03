package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.exp.BaseExpTest;
import org.dflib.exp.map.MapCondition3;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$double;
import static org.junit.jupiter.api.Assertions.*;

public class DoubleTernaryExpTest extends BaseExpTest {

    @Test
    public void creation() {
        assertTrue($double("a").between($double(1), $double(3)) instanceof MapCondition3);
    }

    @Test
    public void getColumnName() {
        Exp<?> exp= $double("a").between($double("b"), $double("c"));
        assertEquals("a between b and c", exp.getColumnName());
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $double(1).between($double(2), $double(3)),
                $double(1).between($double(2), $double(3)),
                $double(1).between($double(2), $double(4)));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $double(1).between($double(2), $double(3)),
                $double(1).between($double(2), $double(3)),
                $double(1).between($double(2), $double(4)));
    }
}
