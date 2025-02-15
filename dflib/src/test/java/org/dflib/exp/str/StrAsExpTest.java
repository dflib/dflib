package org.dflib.exp.str;

import org.dflib.Exp;
import org.dflib.exp.BaseExpTest;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.*;

public class StrAsExpTest extends BaseExpTest  {

    @Test
    public void toQL() {
        assertEquals("a as b", $str("a").as("b").toQL());
    }

    @Test
    public void toQL_Chained() {
        assertEquals("a as c", $str("a").as("b").as("c").toQL());
    }

    @Test
    public void as() {
        Exp<String> as = $str("a").as("b");
        assertSame(as, as.as("b"));
        assertNotSame(as, as.as("c"));
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $str("test").as("a"),
                $str("test").as("a"),
                $str("test").as("b"));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $str("test").as("a"),
                $str("test").as("a"),
                $str("test").as("b"));
    }
}
