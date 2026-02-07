package org.dflib.concat;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static java.util.Arrays.asList;

@Deprecated
public class SeriesConcatTest {

    @Test
    public void concat_Array() {
        Series<String> s1 = Series.of("m", "n");
        Series<String> s2 = Series.of("a", "b");
        Series<String> s3 = Series.of("d");

        Series<String> c = SeriesConcat.concat(s1, s2, s3);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d");
    }

    @Deprecated
    @Test
    public void concat_List() {
        Collection<Series<String>> ss = asList(
                Series.of("m", "n"),
                Series.of("a", "b"),
                Series.of("d"));

        Series<String> c = SeriesConcat.concat(ss);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d");
    }
}
