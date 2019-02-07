package com.nhl.dflib.map;

import org.junit.Test;

import static org.junit.Assert.*;

public class CombinationHashTest {

    @Test
    public void testEquals_Simple() {
        CombinationHash k1 = new CombinationHash("a", "b");
        CombinationHash k2 = new CombinationHash("a", "b");
        CombinationHash k3 = new CombinationHash("a", "c");
        CombinationHash k4 = new CombinationHash("c", "b");
        CombinationHash k5 = new CombinationHash("d", "e");
        CombinationHash k6 = new CombinationHash("b", "a");

        assertEquals(k1, k1);
        assertEquals(k1, k2);
        assertEquals(k2, k1);

        assertNotEquals(k1, k3);
        assertNotEquals(k1, k4);
        assertNotEquals(k1, k5);
        assertNotEquals(k1, k6);
    }

    @Test
    public void testEquals_Nested() {
        CombinationHash k1 = new CombinationHash(new CombinationHash("a", "b"), "c");
        CombinationHash k2 = new CombinationHash(new CombinationHash("a", "b"), "c");
        CombinationHash k3 = new CombinationHash("a", new CombinationHash("b", "c"));
        CombinationHash k4 = new CombinationHash("a", "b");
        CombinationHash k5 = new CombinationHash(new CombinationHash("a", "b"), new CombinationHash("c", "d"));

        assertEquals(k1, k1);
        assertEquals(k1, k2);
        assertEquals(k2, k1);

        assertNotEquals(k1, k3);
        assertNotEquals(k1, k4);
        assertNotEquals(k1, k5);
    }
}
