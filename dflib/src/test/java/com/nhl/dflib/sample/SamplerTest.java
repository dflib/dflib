package com.nhl.dflib.sample;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class SamplerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSampleIndex_SizeTooLarge() {
        IntSeries sample = Sampler.sampleIndex(6, 5, new Random(5));
    }

    @Test
    public void testSampleIndex() {
        // using fixed seed to get reproducible result
        IntSeries sample = Sampler.sampleIndex(5, 10, new Random(5));
        new IntSeriesAsserts(sample).expectData(3, 4, 2, 8, 5);
    }

    @Test
    public void testSampleIndex_SameSize() {
        // using fixed seed to get reproducible result
        IntSeries sample = Sampler.sampleIndex(5, 5, new Random(5));
        new IntSeriesAsserts(sample).expectData(3, 1, 4, 0, 2);
    }

    @Test
    public void testSampleIndex_NonRepeating() {
        IntSeries sample = Sampler.sampleIndex(50, 100, new SecureRandom());
        Set<Integer> seen = new HashSet<>();

        for (int i = 0; i < sample.size(); i++) {
            boolean repeated = !seen.add(i);
            assertFalse(i + " is present at least twice", repeated);
        }
    }

    @Test
    public void testSampleIndex_NonRepeating_BetweenRuns() {
        Random random = new SecureRandom();

        IntSeries s1 = Sampler.sampleIndex(5, 100, random);
        IntSeries s2 = Sampler.sampleIndex(5, 100, random);

        boolean hasDifferences = false;
        for (int i = 0; i < 5; i++) {
            if (s1.getInt(i) != s2.getInt(i)) {
                hasDifferences = true;
                break;
            }
        }

        assertTrue(hasDifferences);
    }

    @Test
    public void testSampleIndex_ImplicitRandom_NonRepeating_BetweenRuns() {
        IntSeries s1 = Sampler.sampleIndex(5, 100);
        IntSeries s2 = Sampler.sampleIndex(5, 100);

        boolean hasDifferences = false;
        for (int i = 0; i < 5; i++) {
            if (s1.getInt(i) != s2.getInt(i)) {
                hasDifferences = true;
                break;
            }
        }

        assertTrue(hasDifferences);
    }
}
