package com.nhl.dflib.sort;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.Series;
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

    public static IntComparator of(Series<?> column, boolean ascending) {
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
