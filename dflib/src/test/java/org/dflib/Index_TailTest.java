package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class Index_TailTest {

    @Test
    public void test() {
        Index s = Index.of("a", "b", "c").tail(2);
        assertArrayEquals(s.toArray(), new String[]{"b", "c"});
    }

    @Test
    public void zero() {
        Index s = Index.of("a", "b", "c").tail(0);
        assertArrayEquals(s.toArray(), new String[0]);
    }

    @Test
    public void outOfBounds() {
        Index s = Index.of("a", "b", "c").tail(4);
        assertArrayEquals(s.toArray(), new String[]{"a", "b", "c"});
    }

    @Test
    public void negative() {
        Index s = Index.of("a", "b", "c").tail(-2);
        assertArrayEquals(s.toArray(), new String[]{"a"});
    }

    @Test
    public void negative_OutOfBounds() {
        Index s = Index.of("a", "b", "c").tail(-4);
        assertArrayEquals(s.toArray(), new String[]{"a", "b", "c"});
    }
}
