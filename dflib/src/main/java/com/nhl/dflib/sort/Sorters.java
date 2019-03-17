package com.nhl.dflib.sort;

import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;

import java.util.Comparator;

public interface Sorters {

    static <V extends Comparable<? super V>> Comparator<RowProxy> sorter(RowToValueMapper<V> sortKeyExtractor) {
        return Comparator.comparing(sortKeyExtractor::map);
    }

    static Comparator<RowProxy> sorter(Index columns, String... sortColumns) {

        if (sortColumns.length == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        Comparator<RowProxy> c = null;
        for (int i = 0; i < sortColumns.length; i++) {
            IndexPosition pos = columns.position(sortColumns[i]);
            Comparator<RowProxy> ci = Comparator.comparing(o -> (Comparable) o.get(pos.ordinal()));
            c = c == null ? ci : c.thenComparing(ci);
        }

        return c;
    }

    static Comparator<RowProxy> sorter(Index columns, int... sortColumns) {

        if (sortColumns.length == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        Comparator<RowProxy> c = null;
        for (int i = 0; i < sortColumns.length; i++) {
            IndexPosition pos = columns.getPositions()[sortColumns[i]];
            Comparator<RowProxy> ci = Comparator.comparing(o -> (Comparable) o.get(pos.ordinal()));
            c = c == null ? ci : c.thenComparing(ci);
        }

        return c;
    }
}
