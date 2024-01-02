package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

public class SeriesTest {

    @Test
    public void forData_Array() {
        new SeriesAsserts(Series.of()).expectData();
        new SeriesAsserts(Series.of("a")).expectData("a");
        new SeriesAsserts(Series.of("a", "b")).expectData("a", "b");
    }

    @Test
    public void forData_Iterable() {
        Iterable<String> it = () -> asList("a", "c", "b").iterator();
        new SeriesAsserts(Series.ofIterable(it)).expectData("a", "c", "b");
    }

    @Test
    public void forData_Iterable_List() {
        new SeriesAsserts(Series.ofIterable(asList("a", "c", "b"))).expectData("a", "c", "b");
    }
}
