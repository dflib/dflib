package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Series_ToListTest extends BaseObjectSeriesTest {

    public Series_ToListTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testContents() {
        List<String> l = createSeries("a", "b", "c", "d", "e").toList();
        assertEquals(asList("a", "b", "c", "d", "e"), l);
    }

    @Test
    public void testMutability() {
        Series<String> s = createSeries("a", "b");
        List<String> l = s.toList();
        l.set(0, "c");

        assertEquals(asList("c", "b"), l);
        new SeriesAsserts(s).expectData("a", "b");
    }

}
