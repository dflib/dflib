package com.nhl.dflib.sort;

import com.nhl.dflib.Index;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;

import java.util.Comparator;

public interface Sorters {

    static <V extends Comparable<? super V>> Comparator<RowProxy> sorter(RowToValueMapper<V> sortKeyExtractor) {
        return Comparator.comparing(sortKeyExtractor::map);
    }

    static Comparator<RowProxy> sorter(Index columns, String sortColumn, boolean ascending) {
        int pos = columns.position(sortColumn);
        Comparator<RowProxy> ci = Comparator.comparing(o -> (Comparable) o.get(pos));
        return ascending ? ci : ci.reversed();
    }

    static Comparator<RowProxy> sorter(Index columns, int sortColumn, boolean ascending) {
        Comparator<RowProxy> ci = Comparator.comparing(o -> (Comparable) o.get(sortColumn));
        return ascending ? ci : ci.reversed();
    }

    static Comparator<RowProxy> sorter(Index columns, String[] sortColumns, boolean[] ascending) {

        if (sortColumns.length == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        Comparator<RowProxy> c = null;
        for (int i = 0; i < sortColumns.length; i++) {
            Comparator<RowProxy> ci = sorter(columns, sortColumns[i], ascending[i]);
            c = c == null ? ci : c.thenComparing(ci);
        }

        return c;
    }

    static Comparator<RowProxy> sorter(Index columns, int[] sortColumns, boolean[] ascending) {

        if (sortColumns.length == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        Comparator<RowProxy> c = null;
        for (int i = 0; i < sortColumns.length; i++) {
            Comparator<RowProxy> ci = sorter(columns, sortColumns[i], ascending[i]);
            c = c == null ? ci : c.thenComparing(ci);
        }

        return c;
    }
}
