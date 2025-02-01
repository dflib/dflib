package org.dflib.exp.num;

import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$double;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dflib.Exp;
import org.dflib.exp.ExpBaseTest;
import org.dflib.exp.map.MapCondition3;

public class DoubleTernaryExpTest extends ExpBaseTest {

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
    public void equalsHashCode() {
        Exp<?> e1 = $double(1).between($double(2), $double(3));
        Exp<?> e2 = $double(1).between($double(2), $double(3));
        Exp<?> e3 = $double(1).between($double(2), $double(3));
        Exp<?> different = $double(1).between($double(2), $double(4));

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different);
    }
}
