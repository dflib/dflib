package org.dflib.exp.num;

import org.dflib.exp.BaseExpTest;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$double;
import static org.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoubleBinaryExpTest extends BaseExpTest {

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
    public void testEquals() {
        assertExpEquals(
                $double(1).add($int(2)),
                $double(1).add($int(2)),
                $double(1).add($int(3)));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $double(1).add($int(2)),
                $double(1).add($int(2)),
                $double(1).add($int(3)));
    }
}
