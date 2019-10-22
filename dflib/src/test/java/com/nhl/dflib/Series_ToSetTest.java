package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Series_ToSetTest extends BaseObjectSeriesTest {

    public Series_ToSetTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testContents() {
        Set<String> set = createSeries("a", "b", "a", "d", "b").toSet();
        assertEquals(new HashSet<>(asList("a", "b", "d")), set);
    }

    @Test
    public void testMutability() {
        Series<String> s = createSeries("a", "b");
        Set<String> set = s.toSet();
        set.remove("b");

        assertEquals(new HashSet<>(asList("a")), set);
        new SeriesAsserts(s).expectData("a", "b");
    }
}
