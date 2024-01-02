package org.dflib.sample;

import org.dflib.IntSeries;
import org.dflib.Series;

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

    public static Random getDefaultRandom() {
        return defaultRandom;
    }

    public static IntSeries sampleIndex(int sampleSize, int originalSize) {
        return sampleIndex(sampleSize, originalSize, defaultRandom);
    }

    public static IntSeries sampleIndex(int sampleSize, int originalSize, Random random) {

        if (sampleSize > originalSize) {
            throw new IllegalArgumentException("Sample size must not be higher than the original size");
        }

        int[] data = intSequence(originalSize);
        shuffle(data, random);
        return Series.ofInt(data).head(sampleSize);
    }

    private static int[] intSequence(int size) {
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = i;
        }

        return data;
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
