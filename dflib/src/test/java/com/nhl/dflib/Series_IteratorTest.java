package com.nhl.dflib;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Series_IteratorTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testIterator(SeriesType type) {
        Iterator<String> it = type.createSeries("a", "b", "c", "d", "e").iterator();

        List<String> vals = new ArrayList<>();
        while (it.hasNext()) {
            String n = it.next();
            vals.add(n);
        }

        assertEquals(asList("a", "b", "c", "d", "e"), vals);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testIterator_BoundaryChecks(SeriesType type) {
        Iterator<String> it = type.createSeries("a", "b").iterator();

        while (it.hasNext()) {
            it.next();
        }

        assertThrows(NoSuchElementException.class, () -> it.next(), "Allowed to read past the end of iterator");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testIterator_Immutable(SeriesType type) {
        Iterator<String> it = type.createSeries("a", "b").iterator();

        while (it.hasNext()) {
            it.next();
            assertThrows(UnsupportedOperationException.class, () -> it.remove(), "Allowed to remove from immutable iterator");
        }
    }
}
