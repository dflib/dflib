package com.nhl.dflib.sample;

import com.nhl.dflib.IntSeries;

import java.security.SecureRandom;
import java.util.Random;

/**
 * An object that provides random sampling facilities to DFLib. By default uses {@link SecureRandom} that may create
 * contention in multi-threaded environments. If this becomes a problem, use the variants of Sampler methods that take
 * a custom Random.
 *
 * @since 0.7
 */
public class Sampler {

    private static Random defaultRandom = new SecureRandom();

    public static IntSeries sampleIndex(int sampleSize, int originalSize) {
        return sampleIndex(sampleSize, originalSize, defaultRandom);
    }

    public static IntSeries sampleIndex(int sampleSize, int originalSize, Random random) {
        int[] sample = sampleInts(sampleSize, originalSize, random);
        return IntSeries.forInts(sample);
    }

    private static int[] sampleInts(int sampleSize, int originalSize, Random random) {

        if (sampleSize > originalSize) {
            throw new IllegalArgumentException("Sample size must not be higher than the original size");
        }

        // Not using Reservoir Sampling (https://en.wikipedia.org/wiki/Reservoir_sampling),
        // as it preserves the order of items, and shuffling it post factum will require more calls to Random..
        // TODO: is there an O(N) algorithm that would produce K non-repeating randomly ordered numbers out of N with
        //  a buffer of size K and no extra copying?

        // fill with sequential numbers
        int[] data = new int[originalSize];
        for (int i = 0; i < originalSize; i++) {
            data[i] = i;
        }

        shuffle(data, random);

        // truncate random numbers to sampleSize if needed
        if (sampleSize == originalSize) {
            return data;
        }

        int[] newData = new int[sampleSize];
        System.arraycopy(data, 0, newData, 0, sampleSize);
        return newData;
    }

    // approximately the same as Collections.shuffle()
    private static void shuffle(int[] ints, Random random) {
        int size = ints.length;

        for (int i = size; i > 1; i--) {
            swap(ints, i - 1, random.nextInt(i));
        }
    }

    private static void swap(int[] ints, int i, int j) {
        int tmp = ints[i];
        ints[i] = ints[j];
        ints[j] = tmp;
    }
}
