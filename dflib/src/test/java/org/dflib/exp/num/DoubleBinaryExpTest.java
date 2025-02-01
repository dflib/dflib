package org.dflib.exp.num;

import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$double;
import static org.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dflib.NumExp;
import org.dflib.exp.ExpBaseTest;

public class DoubleBinaryExpTest extends ExpBaseTest {

    @Test
    public void creation() {
        assertTrue($double("a").add(5) instanceof DoubleExp2);
    }

    @Test
    public void getColumnName() {
        assertEquals("a + castAsDouble(5)", $double("a").add(5).getColumnName());
        assertEquals("a - b", $double("a").sub($double("b")).getColumnName());
        assertEquals("a * castAsDouble(b)", $double("a").mul($int("b")).getColumnName());
        assertEquals("a / 5.0", $double("a").div(5.0).getColumnName());
    }

    @Test
    public void equalsHashCode() {
        NumExp<?> e1 = $double(1).add($int(2));
        NumExp<?> e2 = $double(1).add($int(2));
        NumExp<?> e3 = $double(1).add($int(2));
        NumExp<?> different = $double(1).add($int(3));

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different);
    }
}
