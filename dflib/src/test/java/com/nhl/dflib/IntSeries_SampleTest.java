package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class IntSeries_SampleTest {

    @Test
    public void testSample() {
        // using fixed seed to get reproducible result
        IntSeries sample = IntSeries.forInts(15, 4, 2, 6, 7, 12, 88, 9).sample(4, new Random(6));
        new SeriesAsserts(sample).expectData(88, 15, 9, 7);
    }
}
