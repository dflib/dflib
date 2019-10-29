package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_MapTest extends BaseObjectSeriesTest {

    public Series_MapTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testMap_Value() {
        Series<String> s = createSeries("a", "b", "c").map(String::toUpperCase);
        new SeriesAsserts(s).expectData("A", "B", "C");
    }

    @Test
    public void testMap_DataFrame() {
        DataFrame df = createSeries("a", "b", "c").map(Index.forLabels("upper", "is_c"), (v, r) -> {
            r.set(0, v.toUpperCase());
            r.set(1, v.equals("c"));
        });

        new DataFrameAsserts(df, "upper", "is_c")
                .expectHeight(3)
                .expectRow(0, "A", false)
                .expectRow(1, "B", false)
                .expectRow(2, "C", true);
    }

}
