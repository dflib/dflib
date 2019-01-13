package com.nhl.dflib.map;

import com.nhl.dflib.map.CombinationKey;
import org.junit.Test;

import static org.junit.Assert.*;

public class CombinationKeyTest {

    @Test
    public void testEquals_Simple() {
        CombinationKey k1 = new CombinationKey("a", "b");
        CombinationKey k2 = new CombinationKey("a", "b");
        CombinationKey k3 = new CombinationKey("a", "c");
        CombinationKey k4 = new CombinationKey("c", "b");
        CombinationKey k5 = new CombinationKey("d", "e");
        CombinationKey k6 = new CombinationKey("b", "a");

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
        CombinationKey k1 = new CombinationKey(new CombinationKey("a", "b"), "c");
        CombinationKey k2 = new CombinationKey(new CombinationKey("a", "b"), "c");
        CombinationKey k3 = new CombinationKey("a", new CombinationKey("b", "c"));
        CombinationKey k4 = new CombinationKey("a", "b");
        CombinationKey k5 = new CombinationKey(new CombinationKey("a", "b"), new CombinationKey("c", "d"));

        assertEquals(k1, k1);
        assertEquals(k1, k2);
        assertEquals(k2, k1);

        assertNotEquals(k1, k3);
        assertNotEquals(k1, k4);
        assertNotEquals(k1, k5);
    }
}
