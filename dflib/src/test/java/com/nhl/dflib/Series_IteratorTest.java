package com.nhl.dflib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Series_IteratorTest extends BaseObjectSeriesTest {

    public Series_IteratorTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testIterator() {
        Iterator<String> it = createSeries("a", "b", "c", "d", "e").iterator();

        List<String> vals = new ArrayList<>();
        while (it.hasNext()) {
            String n = it.next();
            vals.add(n);
        }

        assertEquals(asList("a", "b", "c", "d", "e"), vals);
    }

    @Test
    public void testIterator_BoundaryChecks() {
        Iterator<String> it = createSeries("a", "b").iterator();

        while (it.hasNext()) {
            it.next();
        }

        try {
            it.next();
            fail("Allowed to read past the end of iterator");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testIterator_Immutable() {
        Iterator<String> it = createSeries("a", "b").iterator();

        while (it.hasNext()) {
            it.next();

            try {
                it.remove();
                fail("Allowed to remove from immutable iterator");
            } catch (UnsupportedOperationException e) {
                // expected
            }
        }
    }
}
