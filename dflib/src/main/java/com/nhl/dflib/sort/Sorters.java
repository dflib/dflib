package com.nhl.dflib.sort;

import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.map.RowToValueMapper;

import java.util.Comparator;

public interface Sorters {

    static <V extends Comparable<? super V>> Comparator<Object[]> sorter(Index columns, RowToValueMapper<V> m) {
        return Comparator.comparing(o -> m.map(columns, o));
    }

    static Comparator<Object[]> sorter(Index columns, String... sortColumns) {

        if (sortColumns.length == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        Comparator<Object[]> c = null;
        for (int i = 0; i < sortColumns.length; i++) {
            IndexPosition pos = columns.position(sortColumns[i]);
            Comparator<Object[]> ci = Comparator.comparing(o -> (Comparable) pos.get(o));
            c = c == null ? ci : c.thenComparing(ci);
        }

        return c;
    }

    static Comparator<Object[]> sorter(Index columns, int... sortColumns) {

        if (sortColumns.length == 0) {
            throw new IllegalArgumentException("No sort columns");
        }

        Comparator<Object[]> c = null;
        for (int i = 0; i < sortColumns.length; i++) {
            IndexPosition pos = columns.getPositions()[sortColumns[i]];
            Comparator<Object[]> ci = Comparator.comparing(o -> (Comparable) pos.get(o));
            c = c == null ? ci : c.thenComparing(ci);
        }

        return c;
    }
}
