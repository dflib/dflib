package com.nhl.dflib.sort;

import com.nhl.dflib.Index;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;

import java.util.Comparator;

public interface Sorters {

    static <V extends Comparable<? super V>> Comparator<RowProxy> sorter(RowToValueMapper<V> sortKeyExtractor) {
        return (c1, c2) -> Comparators.nullsLastCompare(sortKeyExtractor.map(c1), sortKeyExtractor.map(c2));
    }

    static Comparator<RowProxy> sorter(Index columns, String sortColumn, boolean ascending) {
        int pos = columns.position(sortColumn);
        return sorter(pos, ascending);
    }

    static Comparator<RowProxy> sorter(int sortColumn, boolean ascending) {
        return ascending
                ? (c1, c2) -> Comparators.nullsLastCompare((Comparable) c1.get(sortColumn), (Comparable) c2.get(sortColumn))
                // using "nullsFirstCompare" for descending order, so that nulls come out last in both asc and desc direction
                : (c1, c2) -> Comparators.nullsFirstCompare((Comparable) c2.get(sortColumn), (Comparable) c1.get(sortColumn));
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

    static Comparator<RowProxy> sorter(int[] sortColumns, boolean[] ascending) {

        if (sortColumns.length == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        Comparator<RowProxy> c = null;
        for (int i = 0; i < sortColumns.length; i++) {
            Comparator<RowProxy> ci = sorter(sortColumns[i], ascending[i]);
            c = c == null ? ci : c.thenComparing(ci);
        }

        return c;
    }
}
