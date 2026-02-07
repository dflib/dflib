package org.dflib.concat;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.union.SeriesUnion;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class SeriesUnionTest {

    @Test
    public void of() {
        Series<String> s1 = Series.of("m", "n");
        Series<String> s2 = Series.of("a", "b");
        Series<String> s3 = Series.of("d");

        Series<String> c = SeriesUnion.of(s1, s2, s3);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d");
    }

    @Test
    public void ofInt() {
        IntSeries s1 = Series.ofInt(5, 6, 7);
        IntSeries s2 = Series.ofInt(-1, -2);
        IntSeries s3 = Series.ofInt(0);

        IntSeries c = SeriesUnion.ofInt(s1, s2, s3);
        new SeriesAsserts(c).expectData(5, 6, 7, -1, -2, 0);
    }
}
