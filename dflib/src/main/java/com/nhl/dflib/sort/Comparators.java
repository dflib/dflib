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

        if (w == 0) {

        }

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

    public static IntComparator of(IntSeries column, boolean ascending) {
        return ascending
                ? (i1, i2) -> Integer.compare(column.getInt(i1), column.getInt(i2))
                : (i1, i2) -> Integer.compare(column.getInt(i2), column.getInt(i1));
    }

    public static IntComparator of(LongSeries column, boolean ascending) {
        return ascending
                ? (i1, i2) -> Long.compare(column.getLong(i1), column.getLong(i2))
                : (i1, i2) -> Long.compare(column.getLong(i2), column.getLong(i1));
    }

    public static IntComparator of(DoubleSeries column, boolean ascending) {
        return ascending
                ? (i1, i2) -> Double.compare(column.getDouble(i1), column.getDouble(i2))
                : (i1, i2) -> Double.compare(column.getDouble(i2), column.getDouble(i1));
    }

    public static IntComparator of(BooleanSeries column, boolean ascending) {
        return ascending
                ? (i1, i2) -> Boolean.compare(column.getBoolean(i1), column.getBoolean(i2))
                : (i1, i2) -> Boolean.compare(column.getBoolean(i2), column.getBoolean(i1));
    }

    public static IntComparator of(Series<?> column, boolean ascending) {

        // TODO: create a map of strategies per series type?
        if (column instanceof IntSeries) {
            return of((IntSeries) column, ascending);
        } else if (column instanceof DoubleSeries) {
            return of((DoubleSeries) column, ascending);
        } else if (column instanceof LongSeries) {
            return of((LongSeries) column, ascending);
        } else if (column instanceof BooleanSeries) {
            return of((BooleanSeries) column, ascending);
        }

        return ascending
                ? (i1, i2) -> nullsLastCompare((Comparable) column.get(i1), (Comparable) column.get(i2))
                : (i1, i2) -> nullsLastCompare((Comparable) column.get(i2), (Comparable) column.get(i1));
    }

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
