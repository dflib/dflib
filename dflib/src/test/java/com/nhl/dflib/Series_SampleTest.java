package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Random;


public class Series_SampleTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void sample(SeriesType type) {
        // using fixed seed to get reproducible result
        Series<String> sample = type.createSeries("a", "b", "c", "d", "e", "f", "g").sample(4, new Random(5));
        new SeriesAsserts(sample).expectData("d", "b", "a", "g");
    }
}
