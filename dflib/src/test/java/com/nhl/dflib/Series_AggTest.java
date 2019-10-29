package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Series_AggTest extends BaseObjectSeriesTest {

    public Series_AggTest(BaseObjectSeriesTest.SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testAgg() {
        String aggregated = createSeries("a", "b", "cd", "e", "fg").agg(SeriesAggregator.concat("_"));
        assertEquals("a_b_cd_e_fg", aggregated);
    }

    @Test
    public void testAggMultiple() {

        Series<?> aggregated = createSeries("a", "b", "cd", "e", "fg")
                .aggMultiple(
                        SeriesAggregator.first(),
                        SeriesAggregator.concat("|"),
                        SeriesAggregator.concat("_", "[", "]"),
                        SeriesAggregator.countInt());

        new SeriesAsserts(aggregated).expectData("a", "a|b|cd|e|fg", "[a_b_cd_e_fg]", 5);
    }

    @Test
    public void testFirst() {
        String first = createSeries("a", "b", "cd", "e", "fg").first();
        assertEquals("a", first);
    }

    @Test
    public void testConcat() {
        String concat = createSeries("a", "b", "cd", "e", "fg").concat("_");
        assertEquals("a_b_cd_e_fg", concat);
    }

    @Test
    public void testConcat_PrefixSuffix() {
        String concat = createSeries("a", "b", "cd", "e", "fg").concat("_", "[", "]");
        assertEquals("[a_b_cd_e_fg]", concat);
    }
}
