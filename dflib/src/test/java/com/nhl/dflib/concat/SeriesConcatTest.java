package com.nhl.dflib.concat;

import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static java.util.Arrays.asList;

public class SeriesConcatTest {

    @Test
    public void testConcat_Array() {
        Series<String> s1 = Series.forData("m", "n");
        Series<String> s2 = Series.forData("a", "b");
        Series<String> s3 = Series.forData("d");

        Series<String> c = SeriesConcat.concat(s1, s2, s3);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d");
    }

    @Test
    public void testConcat_List() {
        Collection<Series<String>> ss = asList(
                Series.forData("m", "n"),
                Series.forData("a", "b"),
                Series.forData("d"));

        Series<String> c = SeriesConcat.concat(ss);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d");
    }
}
