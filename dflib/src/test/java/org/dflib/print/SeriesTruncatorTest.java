package org.dflib.print;

import org.dflib.Series;
import org.dflib.series.IntSequenceSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeriesTruncatorTest {

    @Test
    public void create_Odd() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<?> tr = SeriesTruncator.create(s, 5);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(3, tr.head.size());
        assertEquals(2, tr.tail.size());
    }

    @Test
    public void create_Even() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<?> tr = SeriesTruncator.create(s, 6);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(3, tr.head.size());
        assertEquals(3, tr.tail.size());
    }

    @Test
    public void create_Two() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<?> tr = SeriesTruncator.create(s, 2);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(1, tr.head.size());
        assertEquals(1, tr.tail.size());
    }

    @Test
    public void create_One() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<?> tr = SeriesTruncator.create(s, 1);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(1, tr.head.size());
        assertEquals(0, tr.tail.size());
    }

    @Test
    public void create_Zero() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<?> tr = SeriesTruncator.create(s, 0);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(1, tr.head.size());
        assertEquals(0, tr.tail.size());
    }
}
