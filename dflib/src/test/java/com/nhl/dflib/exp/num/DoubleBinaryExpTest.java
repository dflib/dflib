package com.nhl.dflib.exp.num;

import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$double;
import static com.nhl.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoubleBinaryExpTest {

    @Test
    public void testCreation() {
        assertTrue($double("a").add(5) instanceof DoubleExp2);
    }

    @Test
    public void testName() {
        assertEquals("a + castAsDouble(5)", $double("a").add(5).getName());
        assertEquals("a - b", $double("a").sub($double("b")).getName());
        assertEquals("a * castAsDouble(b)", $double("a").mul($int("b")).getName());
        assertEquals("a / 5.0", $double("a").div(5.0).getName());
    }
}
