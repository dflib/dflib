package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class Index_HeadTest {

    @Test
    public void test() {
        Index s = Index.of("a", "b", "c").head(2);
        assertArrayEquals(s.toArray(), new String[]{"a", "b"});
    }

    @Test
    public void zero() {
        Index s = Index.of("a", "b", "c").head(0);
        assertArrayEquals(s.toArray(), new String[0]);
    }

    @Test
    public void outOfBounds() {
        Index s = Index.of("a", "b", "c").head(4);
        assertArrayEquals(s.toArray(), new String[]{"a", "b", "c"});
    }

    @Test
    public void negative() {
        Index s = Index.of("a", "b", "c").head(-2);
        assertArrayEquals(s.toArray(), new String[]{"c"});
    }

    @Test
    public void negative_OutOfBounds() {
        Index s = Index.of("a", "b", "c").head(-4);
        assertArrayEquals(s.toArray(), new String[]{"a", "b", "c"});
    }
}
