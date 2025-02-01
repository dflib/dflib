package org.dflib.exp;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.*;

public class AsExpTest extends ExpBaseTest {

    @Test
    public void toQL() {
        assertEquals("a as b", Exp.$col("a").as("b").toQL());
    }

    @Test
    public void toQL_Chained() {
        assertEquals("a as c", Exp.$col("a").as("b").as("c").toQL());
    }

    @Test
    public void as() {
        Exp<String> as = Exp.$str("a").as("b");
        assertSame(as, as.as("b"));
        assertNotSame(as, as.as("c"));
    }

    @Test
    public void equalsHashCode() {
        Exp<String> e1 = $str("test").as("a");
        Exp<String> e2 = $str("test").as("a");
        Exp<String> e3 = $str("test").as("a");
        Exp<String> different = $str("test").as("b");

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different);
    }

}
