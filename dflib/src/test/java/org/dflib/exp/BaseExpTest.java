package org.dflib.exp;

import org.dflib.Exp;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseExpTest {

    protected void assertExpEquals(Exp<?> eq1, Exp<?> eq2, Exp<?> notEq) {
        assertEquals(eq1, eq1, "Reflexive: object must equal itself");

        assertEquals(eq1, eq2, "Symmetric: x should equal y");
        assertEquals(eq2, eq1, "Symmetric: y should equal x");

        assertNotEquals(eq1, null);
        assertNotEquals(eq1, notEq);
        assertNotEquals(eq2, notEq);
    }

    protected void assertExpHashCode(Exp<?> eq1, Exp<?> eq2, Exp<?> notEq) {
        assertEquals(eq1.hashCode(), eq1.hashCode(), "HashCode must be consistent");
        assertEquals(eq1.hashCode(), eq2.hashCode(), "Equal objects must have equal hashCodes");
        assertNotEquals(eq1.hashCode(), notEq.hashCode());

        assertExpHashCode(eq1);
        assertExpHashCode(eq2);
        assertExpHashCode(notEq);
    }

    private void assertExpHashCode(Exp<?> e) {
        int hash1 = e.hashCode();
        int hash2 = e.hashCode();
        int hash3 = e.hashCode();
        assertEquals(hash1, hash2, "HashCode must be consistent across multiple calls");
        assertEquals(hash2, hash3, "HashCode must be consistent across multiple calls");

        assertNotEquals(0, hash1, "HashCode should not be 0");
        assertNotEquals(1, hash1, "HashCode should not be 1");
        assertNotEquals(e.getClass().getName().hashCode(), hash1,
                "HashCode should not simply be the class name hash");

        assertNotEquals(System.identityHashCode(e), hash1,
                "HashCode should be overridden and not use default Object.hashCode()");
    }
}
