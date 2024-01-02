package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Series_ByElementTest {

    @Test
    public void test() {

        Series<String> s = Series
                .byElement(Extractor.<String>$col())
                .appender()
                .append(List.of("a", "c", "e"))
                .toSeries();

        new SeriesAsserts(s).expectData("a", "c", "e");
    }

    @Test
    public void smallCapacity() {

        Series<String> s = Series
                .byElement(Extractor.<String>$col())
                .capacity(1)
                .appender()
                .append("a")
                .append("b")
                .append("c")
                .append(List.of("d", "e"))
                .toSeries();

        new SeriesAsserts(s).expectData("a", "b", "c", "d", "e");
    }

    @Test
    public void convertingExtractor() {

        Series<Integer> s = Series
                .byElement(Extractor.$int((String str) -> Integer.parseInt(str)))
                .appender()
                .append(List.of("1", "55", "6"))
                .toSeries();

        assertTrue(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(1, 55, 6);
    }
}
