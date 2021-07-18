package com.nhl.dflib.sort;

import com.nhl.dflib.*;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.row.DataFrameRowProxy;
import com.nhl.dflib.row.RowProxy;

import java.util.Comparator;

/**
 * A factory of comparators whose inputs are row position indices, and that are applied to individual columns.
 * Comparators are composable so ordering by multiple columns can be specified.
 *
 * @since 0.11
 */
public final class Comparators {

    public static IntComparator of(DataFrame df, Sorter[] sorters) {
        int w = sorters.length;

        switch (w) {
            case 0:
                throw new IllegalArgumentException("No sort columns");
            case 1:
                return sorters[0].eval(df);
            default:
                IntComparator sorter = null;
                for (int i = 0; i < w; i++) {
                    IntComparator ci = sorters[i].eval(df);
                    sorter = sorter == null ? ci : sorter.thenComparing(ci);
                }

                return sorter;
        }
    }

    public static IntComparator of(Series s, Sorter[] sorters) {
        int w = sorters.length;

        switch (w) {
            case 0:
                throw new IllegalArgumentException("No sort columns");
            case 1:
                return sorters[0].eval(s);
            default:
                IntComparator sorter = null;
                for (int i = 0; i < w; i++) {
                    IntComparator ci = sorters[i].eval(s);
                    sorter = sorter == null ? ci : sorter.thenComparing(ci);
                }

                return sorter;
        }
    }

    public static IntComparator of(DataFrame df, String[] columns, boolean[] ascending) {
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

    public static IntComparator of(DataFrame df, int[] columns, boolean[] ascending) {
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

    public static IntComparator of(IntSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Integer.compare(s.getInt(i1), s.getInt(i2))
                : (i1, i2) -> Integer.compare(s.getInt(i2), s.getInt(i1));
    }

    public static IntComparator of(LongSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Long.compare(s.getLong(i1), s.getLong(i2))
                : (i1, i2) -> Long.compare(s.getLong(i2), s.getLong(i1));
    }

    public static IntComparator of(DoubleSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Double.compare(s.getDouble(i1), s.getDouble(i2))
                : (i1, i2) -> Double.compare(s.getDouble(i2), s.getDouble(i1));
    }

    public static IntComparator of(BooleanSeries s, boolean ascending) {
        return ascending
                ? (i1, i2) -> Boolean.compare(s.getBoolean(i1), s.getBoolean(i2))
                : (i1, i2) -> Boolean.compare(s.getBoolean(i2), s.getBoolean(i1));
    }

    public static <T> IntComparator of(Series<T> s, Comparator<? super T> comparator) {
        return (i1, i2) -> comparator.compare(s.get(i1), s.get(i2));
    }

    public static IntComparator of(Series<?> s, boolean ascending) {

        // TODO: create a map of strategies per series type?
        if (s instanceof IntSeries) {
            return of((IntSeries) s, ascending);
        } else if (s instanceof DoubleSeries) {
            return of((DoubleSeries) s, ascending);
        } else if (s instanceof LongSeries) {
            return of((LongSeries) s, ascending);
        } else if (s instanceof BooleanSeries) {
            return of((BooleanSeries) s, ascending);
        }

        return ascending
                ? (i1, i2) -> nullsLastCompare((Comparable) s.get(i1), (Comparable) s.get(i2))
                : (i1, i2) -> nullsLastCompare((Comparable) s.get(i2), (Comparable) s.get(i1));
    }

    /**
     * @deprecated since 0.12 as sorting by RowToValueMapper is redundant, and can be expressed as a Sorter.
     */
    @Deprecated
    public static <V extends Comparable<? super V>> IntComparator of(DataFrame df, RowToValueMapper<V> sortKeyExtractor) {

        // slower row-based comparator

        Comparator<RowProxy> rowComparator = (c1, c2) -> nullsLastCompare(sortKeyExtractor.map(c1), sortKeyExtractor.map(c2));

        DataFrameRowProxy p1 = new DataFrameRowProxy(df);
        DataFrameRowProxy p2 = new DataFrameRowProxy(df);
        return (i1, i2) -> rowComparator.compare(p1.rewind(i1), p2.rewind(i2));
    }

    static <V extends Comparable<? super V>> int nullsLastCompare(V a, V b) {

        if (a == null) {
            return (b == null) ? 0 : 1;
        } else if (b == null) {
            return -1;
        } else {
            return a.compareTo(b);
        }
    }
}
