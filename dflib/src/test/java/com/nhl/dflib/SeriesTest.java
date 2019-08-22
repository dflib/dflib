package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static java.util.Arrays.asList;

public class SeriesTest {

    @Test
    public void testForData_Array() {
        new SeriesAsserts(Series.forData()).expectData();
        new SeriesAsserts(Series.forData("a")).expectData("a");
        new SeriesAsserts(Series.forData("a", "b")).expectData("a", "b");
    }

    @Test
    public void testForData_Iterable() {
        Iterable<String> it = () -> asList("a", "c", "b").iterator();
        new SeriesAsserts(Series.forData(it)).expectData("a", "c", "b");
    }

    @Test
    public void testForData_Iterable_List() {
        new SeriesAsserts(Series.forData(asList("a", "c", "b"))).expectData("a", "c", "b");
    }
}
