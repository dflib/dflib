package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Series_ToArrayTest extends BaseObjectSeriesTest {

    public Series_ToArrayTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testContents() {
        String[] a = createSeries("a", "b", "c", "d", "e").toArray(new String[0]);
        assertArrayEquals(new Object[]{"a", "b", "c", "d", "e"}, a);
    }

    @Test
    public void testMutability() {
        Series<String> s = createSeries("a", "b");
        String[] a = s.toArray(new String[0]);
        a[0] = "c";

        new SeriesAsserts(s).expectData("a", "b");
    }
}
