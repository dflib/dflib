package org.dflib.agg;

import java.util.Arrays;


/**
 * @deprecated in favor of {@link Percentiles}
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class PrimitiveSeriesMedian {

    public static double medianOfRange(int first, int lastExclusive) {

        int len = lastExclusive - first;

        switch (len) {
            case 0:
                return first;
            default:
                int m = len / 2;

                int odd = len % 2;
                if (odd == 1) {
                    return first + m;
                }

                return first + m - 0.5;
        }
    }

    public static double medianOfArray(int[] ints, int start, int len) {

        // TODO: there are various ideas for finding a median without creating a sorted copy
        //   e.g. https://stackoverflow.com/questions/33964676/find-the-median-of-an-unsorted-array-without-sorting
        //   https://stackoverflow.com/questions/4201292/on-algorithm-to-find-the-median-of-a-collection-of-numbers

        switch (len) {
            case 0:
                return 0.; // is this reasonable?
            case 1:
                return ints[0];
            default:
                int[] copy = new int[len];
                System.arraycopy(ints, start, copy, 0, len);
                Arrays.sort(copy);

                int m = len / 2;

                int odd = len % 2;
                if (odd == 1) {
                    return copy[m];
                }

                double d1 = copy[m - 1];
                double d2 = copy[m];
                return d1 + (d2 - d1) / 2.;
        }
    }

    public static double medianOfArray(long[] longs, int start, int len) {

        // TODO: there are various ideas for finding a median without creating a sorted copy
        //   e.g. https://stackoverflow.com/questions/33964676/find-the-median-of-an-unsorted-array-without-sorting
        //   https://stackoverflow.com/questions/4201292/on-algorithm-to-find-the-median-of-a-collection-of-numbers

        switch (len) {
            case 0:
                return 0.; // is this reasonable?
            case 1:
                return longs[0];
            default:
                long[] copy = new long[len];
                System.arraycopy(longs, start, copy, 0, len);
                Arrays.sort(copy);

                int m = len / 2;

                int odd = len % 2;
                if (odd == 1) {
                    return copy[m];
                }

                double d1 = copy[m - 1];
                double d2 = copy[m];
                return d1 + (d2 - d1) / 2.;
        }
    }

    /**
     * @since 1.1.0
     */
    public static float medianOfArray(float[] vals, int start, int len) {

        // TODO: there are various ideas for finding a median without creating a sorted copy
        //   e.g. https://stackoverflow.com/questions/33964676/find-the-median-of-an-unsorted-array-without-sorting
        //   https://stackoverflow.com/questions/4201292/on-algorithm-to-find-the-median-of-a-collection-of-numbers

        switch (len) {
            case 0:
                return 0.f; // is this reasonable?
            case 1:
                return vals[0];
            default:
                float[] copy = new float[len];
                System.arraycopy(vals, start, copy, 0, len);
                Arrays.sort(copy);

                int m = len / 2;

                int odd = len % 2;
                if (odd == 1) {
                    return copy[m];
                }

                float d1 = copy[m - 1];
                float d2 = copy[m];
                return d1 + (d2 - d1) / 2f;
        }
    }

    public static double medianOfArray(double[] doubles, int start, int len) {

        // TODO: there are various ideas for finding a median without creating a sorted copy
        //   e.g. https://stackoverflow.com/questions/33964676/find-the-median-of-an-unsorted-array-without-sorting
        //   https://stackoverflow.com/questions/4201292/on-algorithm-to-find-the-median-of-a-collection-of-numbers

        switch (len) {
            case 0:
                return 0.; // is this reasonable?
            case 1:
                return doubles[0];
            default:
                double[] copy = new double[len];
                System.arraycopy(doubles, start, copy, 0, len);
                Arrays.sort(copy);

                int m = len / 2;

                int odd = len % 2;
                if (odd == 1) {
                    return copy[m];
                }

                double d1 = copy[m - 1];
                double d2 = copy[m];
                return d1 + (d2 - d1) / 2.;
        }
    }
}
