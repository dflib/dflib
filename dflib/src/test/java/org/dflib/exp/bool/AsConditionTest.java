package org.dflib.exp.bool;

import org.dflib.Condition;
import org.dflib.exp.BaseExpTest;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$bool;
import static org.junit.jupiter.api.Assertions.*;

public class AsConditionTest extends BaseExpTest  {

    @Test
    public void toQL() {
        assertEquals("a as b", $bool("a").as("b").toQL());
    }

    @Test
    public void toQL_Chained() {
        assertEquals("a as c", $bool("a").as("b").as("c").toQL());
    }

    @Test
    public void as() {
        Condition as = $bool("a").as("b");
        assertSame(as, as.as("b"));
        assertNotSame(as, as.as("c"));
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $bool("test").as("a"),
                $bool("test").as("a"),
                $bool("test").as("b"));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $bool("test").as("a"),
                $bool("test").as("a"),
                $bool("test").as("b"));
    }
}
