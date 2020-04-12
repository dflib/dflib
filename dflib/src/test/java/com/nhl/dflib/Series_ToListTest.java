package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Series_ToListTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testContents(SeriesType type) {
        List<String> l = type.createSeries("a", "b", "c", "d", "e").toList();
        assertEquals(asList("a", "b", "c", "d", "e"), l);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testMutability(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        List<String> l = s.toList();
        l.set(0, "c");

        assertEquals(asList("c", "b"), l);
        new SeriesAsserts(s).expectData("a", "b");
    }

}
