package org.dflib.concat;

import org.dflib.BooleanSeries;
import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.series.ArraySeries;

import java.util.stream.StreamSupport;

public class SeriesConcat {

    /**
     * Concatenates multiple Series of various length into one. Attempts to optimize the operation for cases when all
     * Series arguments are of the same primitive type.
     */
    public static <T> Series<T> concat(Series<T>... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return Series.of();
        } else if (clen == 1) {
            return concat[0];
        }

        // attempt to concat as primitive series
        // TODO: use widening conversions to preserve primitive series in cases like IntSeries + LongSeries, etc.

        if (concat[0] instanceof IntSeries) {
            for (int i = 1; i < clen; i++) {
                if (!(concat[i] instanceof IntSeries)) {
                    return concatAsObjects(concat);
                }
            }

            IntSeries[] ints = new IntSeries[clen];
            System.arraycopy(concat, 0, ints, 0, clen);
            return (Series<T>) intConcat(ints);
        }

        if (concat[0] instanceof LongSeries) {
            for (int i = 1; i < clen; i++) {
                if (!(concat[i] instanceof LongSeries)) {
                    return concatAsObjects(concat);
                }
            }

            LongSeries[] longs = new LongSeries[clen];
            System.arraycopy(concat, 0, longs, 0, clen);
            return (Series<T>) longConcat(longs);
        }

        if (concat[0] instanceof BooleanSeries) {
            for (int i = 1; i < clen; i++) {
                if (!(concat[i] instanceof BooleanSeries)) {
                    return concatAsObjects(concat);
                }
            }

            BooleanSeries[] bools = new BooleanSeries[clen];
            System.arraycopy(concat, 0, bools, 0, clen);
            return (Series<T>) boolConcat(bools);
        }

        if (concat[0] instanceof FloatSeries) {
            for (int i = 1; i < clen; i++) {
                if (!(concat[i] instanceof FloatSeries)) {
                    return concatAsObjects(concat);
                }
            }

            FloatSeries[] floats = new FloatSeries[clen];
            System.arraycopy(concat, 0, floats, 0, clen);
            return (Series<T>) floatConcat(floats);
        }

        if (concat[0] instanceof DoubleSeries) {
            for (int i = 1; i < clen; i++) {
                if (!(concat[i] instanceof DoubleSeries)) {
                    return concatAsObjects(concat);
                }
            }

            DoubleSeries[] doubles = new DoubleSeries[clen];
            System.arraycopy(concat, 0, doubles, 0, clen);
            return (Series<T>) doubleConcat(doubles);
        }

        return concatAsObjects(concat);
    }

    public static <T> Series<T> concat(Iterable<Series<T>> concat) {
        return concat(StreamSupport.stream(concat.spliterator(), false).toArray(Series[]::new));
    }

    public static IntSeries intConcat(IntSeries... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return Series.ofInt();
        } else if (clen == 1) {
            return concat[0];
        }

        int h = 0;
        for (IntSeries s : concat) {
            h += s.size();
        }

        int[] data = new int[h];
        int offset = 0;
        for (IntSeries s : concat) {
            int len = s.size();
            s.copyToInt(data, 0, offset, len);
            offset += len;
        }

        return Series.ofInt(data);
    }

    public static IntSeries intConcat(Iterable<IntSeries> concat) {
        int h = 0;
        int total = 0;
        for (IntSeries s : concat) {
            h += s.size();
            total++;
        }

        if (total == 1) {
            return concat.iterator().next();
        }

        int[] data = new int[h];
        int offset = 0;
        for (IntSeries s : concat) {
            int len = s.size();
            s.copyToInt(data, 0, offset, len);
            offset += len;
        }

        return Series.ofInt(data);
    }

    /**
     * @since 1.3.0
     */
    public static LongSeries longConcat(LongSeries... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return Series.ofLong();
        } else if (clen == 1) {
            return concat[0];
        }

        int h = 0;
        for (LongSeries s : concat) {
            h += s.size();
        }

        long[] data = new long[h];
        int offset = 0;
        for (LongSeries s : concat) {
            int len = s.size();
            s.copyToLong(data, 0, offset, len);
            offset += len;
        }

        return Series.ofLong(data);
    }

    /**
     * @since 1.3.0
     */
    public static FloatSeries floatConcat(FloatSeries... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return Series.ofFloat();
        } else if (clen == 1) {
            return concat[0];
        }

        int h = 0;
        for (FloatSeries s : concat) {
            h += s.size();
        }

        float[] data = new float[h];
        int offset = 0;
        for (FloatSeries s : concat) {
            int len = s.size();
            s.copyToFloat(data, 0, offset, len);
            offset += len;
        }

        return Series.ofFloat(data);
    }

    /**
     * @since 1.3.0
     */
    public static DoubleSeries doubleConcat(DoubleSeries... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return Series.ofDouble();
        } else if (clen == 1) {
            return concat[0];
        }

        int h = 0;
        for (DoubleSeries s : concat) {
            h += s.size();
        }

        double[] data = new double[h];
        int offset = 0;
        for (DoubleSeries s : concat) {
            int len = s.size();
            s.copyToDouble(data, 0, offset, len);
            offset += len;
        }

        return Series.ofDouble(data);
    }

    /**
     * @since 1.3.0
     */
    public static BooleanSeries boolConcat(BooleanSeries... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return Series.ofBool();
        } else if (clen == 1) {
            return concat[0];
        }

        int h = 0;
        for (BooleanSeries s : concat) {
            h += s.size();
        }

        boolean[] data = new boolean[h];
        int offset = 0;
        for (BooleanSeries s : concat) {
            int len = s.size();
            s.copyToBool(data, 0, offset, len);
            offset += len;
        }

        return Series.ofBool(data);
    }

    /**
     * Performs concatenation of Series without checking for possible optimizations related to primitive series.
     *
     * @since 1.3.0
     */
    public static <T> Series<T> concatAsObjects(Series<T>... concat) {

        int h = 0;
        for (Series<? extends T> s : concat) {
            h += s.size();
        }

        T[] data = (T[]) new Object[h];
        int offset = 0;
        for (Series<? extends T> s : concat) {
            int len = s.size();
            s.copyTo(data, 0, offset, len);
            offset += len;
        }

        return new ArraySeries<>(data);
    }
}
