package org.dflib;

import org.dflib.unit.IndexAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IndexTest {

    @Test
    public void testEquals() {
        Index i1 = Index.of("a", "b", "c");
        Index i2 = Index.of("a", "b", "c");
        Index i3 = Index.of("a", "b", "C");

        assertEquals(i1, i1);
        assertEquals(i1, i2);
        assertNotEquals(i1, i3);
    }

    @Test
    public void testHashCode() {
        Index i1 = Index.of("a", "b", "c");
        Index i2 = Index.of("a", "b", "c");
        Index i3 = Index.of("a", "b", "C");

        assertEquals(i1.hashCode(), i1.hashCode());
        assertEquals(i1.hashCode(), i2.hashCode());
        assertNotEquals(i1.hashCode(), i3.hashCode());
    }

    @Test
    public void expand() {
        Index i = Index.of("a", "b", "c").expand("d", "e", "d", "a");
        new SeriesAsserts(i.toSeries()).expectData("a", "b", "c", "d", "e", "d_", "a_");
    }

    @Test
    public void positionsArray() {
        assertArrayEquals(new int[]{0, 1, 2, 3}, Index.positionsArray(4));
    }

    @Test
    public void positions_ByLabel() {
        Index i = Index.of("a", "b", "c", "d");

        assertArrayEquals(new int[0], i.positions());
        assertArrayEquals(new int[]{0, 2}, i.positions("a", "c"));
        assertArrayEquals(new int[]{2, 0}, i.positions("c", "a"));
    }

    @Test
    public void positionsExcept_ByLabel() {
        Index i = Index.of("a", "b", "c", "d");

        assertArrayEquals(new int[]{0, 1, 2, 3}, i.positionsExcept(new String[0]));
        assertArrayEquals(new int[]{1, 3}, i.positionsExcept("a", "c"));
        assertArrayEquals(new int[0], i.positionsExcept("a", "b", "c", "d"));
    }

    @Test
    public void positionsExcept_ByPos() {
        Index i = Index.of("a", "b", "c", "d");

        assertArrayEquals(new int[]{0, 1, 2, 3}, i.positionsExcept(new int[0]));
        assertArrayEquals(new int[]{0, 2}, i.positionsExcept(1, 3));
        assertArrayEquals(new int[0], i.positionsExcept(0, 1, 3, 2));
    }

    @Test
    public void positions_Predicated() {
        Index i = Index.of("a", "b", "c", "d");
        assertArrayEquals(new int[]{2, 3}, i.positions(c -> c.charAt(0) >= 'c'));
    }

    @Test
    public void positions_NoLabel() {
        Index i = Index.of("a", "b");
        assertThrows(IllegalArgumentException.class, () -> i.positions("a", "c"));
    }

    @Test
    public void withNames_Enum() {
        Index i = Index.of(E1.class);
        IndexAsserts.expect(i, "a", "b", "c");
    }

    @Test
    public void selectRange0() {
        Index i = Index.of("a", "b", "c", "d").selectRange(1, 3);
        IndexAsserts.expect(i, "b", "c");
    }

    @Test
    public void selectRange1() {
        Index i = Index.of("a", "b", "c", "d").selectRange(0, 4);
        IndexAsserts.expect(i, "a", "b", "c", "d");
    }

    @Test
    public void selectRangeOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> Index.of("a", "b", "c", "d").selectRange(0, 5));
    }

    @Test
    public void toSeries() {
        Series<String> s = Index.of("a", "b", "c", "d").toSeries();
        new SeriesAsserts(s).expectData("a", "b", "c", "d");
    }

    enum E1 {
        a, b, c
    }
}
