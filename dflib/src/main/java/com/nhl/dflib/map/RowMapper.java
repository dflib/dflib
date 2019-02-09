package com.nhl.dflib.map;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface RowMapper {

    static RowMapper copy() {
        return (c, sr, tr) -> c.getSourceIndex().compactCopy(sr, tr, 0);
    }

    static RowMapper mapColumn(String column, RowToValueMapper<?> m) {
        return (c, sr, tr) -> {
            c.getSourceIndex().compactCopy(sr, tr, 0);
            c.set(tr, column, m.map(c.getSourceIndex(), sr));
        };
    }

    static RowMapper mapColumn(int column, RowToValueMapper<?> m) {
        return (c, sr, tr) -> {
            c.getSourceIndex().compactCopy(sr, tr, 0);
            c.set(tr, column, m.map(c.getSourceIndex(), sr));
        };
    }

    static <V> RowMapper mapColumn(String column, ValueMapper<V, ?> m) {
        return (c, sr, tr) -> {
            c.getSourceIndex().compactCopy(sr, tr, 0);
            c.set(tr, column, m.map((V) c.get(sr, column)));
        };
    }

    static <V> RowMapper mapColumn(int column, ValueMapper<V, ?> m) {
        return (c, sr, tr) -> {
            c.getSourceIndex().compactCopy(sr, tr, 0);
            c.set(tr, column, m.map((V) c.get(sr, column)));
        };
    }

    static <V> RowMapper addColumns(RowToValueMapper<?>... valueProducers) {
        return (c, sr, tr) -> {

            Index sourceIndex = c.getSourceIndex();

            c.getSourceIndex().compactCopy(sr, tr, 0);

            int oldWidth = sourceIndex.size();
            int expansionWidth = valueProducers.length;

            for (int i = 0; i < expansionWidth; i++) {
                c.set(tr, oldWidth + i, valueProducers[i].map(sourceIndex, sr));
            }
        };
    }

    void map(MapContext c, Object[] sourceRow, Object[] targetRow);

    default RowMapper and(RowMapper m) {
        return (c, sr, tr) -> {
            this.map(c, sr, tr);
            m.map(c, sr, tr);
        };
    }
}
