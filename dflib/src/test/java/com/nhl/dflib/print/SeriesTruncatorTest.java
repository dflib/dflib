package com.nhl.dflib.print;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.IntSequenceSeries;
import org.junit.Test;

import static org.junit.Assert.*;

public class SeriesTruncatorTest {

    @Test
    public void testCreate_Odd() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<Integer> tr = SeriesTruncator.create(s, 5);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(3, tr.head);
        assertEquals(2, tr.tail);
    }

    @Test
    public void testCreate_Even() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<Integer> tr = SeriesTruncator.create(s, 6);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(3, tr.head);
        assertEquals(3, tr.tail);
    }

    @Test
    public void testCreate_Two() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<Integer> tr = SeriesTruncator.create(s, 2);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(1, tr.head);
        assertEquals(1, tr.tail);
    }

    @Test
    public void testCreate_One() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<Integer> tr = SeriesTruncator.create(s, 1);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(1, tr.head);
        assertEquals(0, tr.tail);
    }

    @Test
    public void testCreate_Zero() {
        Series<Integer> s = new IntSequenceSeries(0, 10);
        SeriesTruncator<Integer> tr = SeriesTruncator.create(s, 0);

        assertSame(s, tr.series);
        assertTrue(tr.truncated);
        assertEquals(0, tr.head);
        assertEquals(0, tr.tail);
    }
}
