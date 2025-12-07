package org.dflib.sort;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.series.IntArraySeries;

import java.util.Comparator;
import java.util.Objects;

public interface IntComparator {

    /**
     * @since 2.0.0
     */
    static IntComparator of(DataFrame df, Sorter[] sorters) {
        int w = sorters.length;

        switch (w) {
            case 0:
                throw new IllegalArgumentException("No sort columns");
            case 1:
                return sorters[0].eval(df);
            default:
                IntComparator sorter = null;
                for (Sorter value : sorters) {
                    IntComparator ci = value.eval(df);
                    sorter = sorter == null ? ci : sorter.thenComparing(ci);
                }

                return sorter;
        }
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(Series<?> s, Sorter[] sorters) {
        int w = sorters.length;

        switch (w) {
            case 0:
                throw new IllegalArgumentException("No sort columns");
            case 1:
                return sorters[0].eval(s);
            default:
                IntComparator sorter = null;
                for (Sorter value : sorters) {
                    IntComparator ci = value.eval(s);
                    sorter = sorter == null ? ci : sorter.thenComparing(ci);
                }

                return sorter;
        }
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(DataFrame df, String[] columns, boolean[] ascending) {
        int w = columns.length;

        if (w == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        IntComparator sorter = null;
        for (int i = 0; i < w; i++) {
            IntComparator ci = of(df.getColumn(columns[i]), ascending[i]);
            sorter = sorter == null ? ci : sorter.thenComparing(ci);
        }

        return sorter;
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(DataFrame df, int[] columns, boolean[] ascending) {
        int w = columns.length;

        if (w == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        IntComparator sorter = null;
        for (int i = 0; i < w; i++) {
            IntComparator ci = of(df.getColumn(columns[i]), ascending[i]);
            sorter = sorter == null ? ci : sorter.thenComparing(ci);
        }

        return sorter;
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(IntSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Integer.compare(s.getInt(i1), s.getInt(i2))
                : (i1, i2) -> Integer.compare(s.getInt(i2), s.getInt(i1));
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(LongSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Long.compare(s.getLong(i1), s.getLong(i2))
                : (i1, i2) -> Long.compare(s.getLong(i2), s.getLong(i1));
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(FloatSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Float.compare(s.getFloat(i1), s.getFloat(i2))
                : (i1, i2) -> Float.compare(s.getFloat(i2), s.getFloat(i1));
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(DoubleSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Double.compare(s.getDouble(i1), s.getDouble(i2))
                : (i1, i2) -> Double.compare(s.getDouble(i2), s.getDouble(i1));
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(BooleanSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Boolean.compare(s.getBool(i1), s.getBool(i2))
                : (i1, i2) -> Boolean.compare(s.getBool(i2), s.getBool(i1));
    }

    /**
     * @since 2.0.0
     */
    static <T> IntComparator of(Series<T> s, Comparator<? super T> comparator) {
        return (i1, i2) -> comparator.compare(s.get(i1), s.get(i2));
    }

    /**
     * @since 2.0.0
     */
    static IntComparator of(Series<?> s, boolean ascending) {

        // TODO: create a map of strategies per series type?
        if (s instanceof IntSeries) {
            return of((IntSeries) s, ascending);
        } else if (s instanceof DoubleSeries) {
            return of((DoubleSeries) s, ascending);
        } else if (s instanceof LongSeries) {
            return of((LongSeries) s, ascending);
        } else if (s instanceof BooleanSeries) {
            return of((BooleanSeries) s, ascending);
        } else if (s instanceof FloatSeries) {
            return of((FloatSeries) s, ascending);
        }

        return ascending
                ? (i1, i2) -> IntComparators.nullsLastCompare((Comparable) s.get(i1), (Comparable) s.get(i2))
                : (i1, i2) -> IntComparators.nullsLastCompare((Comparable) s.get(i2), (Comparable) s.get(i1));
    }

    /**
     * @since 2.0.0
     */
    default IntSeries sortIndex(int size) {
        int[] mutableIndex = IntComparators.sequence(size);
        IntTimSort.sort(mutableIndex, this);
        return new IntArraySeries(mutableIndex);
    }

    int compare(int i1, int i2);

    default IntComparator thenComparing(IntComparator other) {
        Objects.requireNonNull(other);
        return (i1, i2) -> {
            int res = compare(i1, i2);
            return res != 0 ? res : other.compare(i1, i2);
        };
    }
}
