package org.dflib.exp;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.*;

public class AsExpTest extends BaseExpTest {

    @Test
    public void toQL() {
        assertEquals("a as b", $col("a").as("b").toQL());
    }

    @Test
    public void toQL_Chained() {
        assertEquals("a as c", $col("a").as("b").as("c").toQL());
    }

    @Test
    public void as() {
        Exp<Object> as = $col("a").as("b");
        assertSame(as, as.as("b"));
        assertNotSame(as, as.as("c"));
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $col("test").as("a"),
                $col("test").as("a"),
                $col("test").as("b"));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $col("test").as("a"),
                $col("test").as("a"),
                $col("test").as("b"));
    }
}
