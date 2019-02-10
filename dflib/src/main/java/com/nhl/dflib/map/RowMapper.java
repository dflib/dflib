package com.nhl.dflib.map;

import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.row.RowBuilder;

@FunctionalInterface
public interface RowMapper {

    static RowMapper copy() {
        return (s, t) -> s.copyTo(t);
    }

    static RowMapper mapColumn(String column, RowToValueMapper<?> m) {
        return (s, t) -> {
            s.copyTo(t);
            t.set(column, m.map(s));
        };
    }

    static RowMapper mapColumn(int column, RowToValueMapper<?> m) {
        return (s, t) -> {
            s.copyTo(t);
            t.set(column, m.map(s));
        };
    }

    static <V> RowMapper mapColumnValue(String column, ValueMapper<V, ?> m) {
        return (s, t) -> {
            s.copyTo(t);
            t.set(column, m.map((V) s.get(column)));
        };
    }

    static <V> RowMapper mapColumnValue(int column, ValueMapper<V, ?> m) {
        return (s, t) -> {
            s.copyTo(t);
            t.set(column, m.map((V) s.get(column)));
        };
    }

    static <V> RowMapper addColumns(RowToValueMapper<?>... valueProducers) {
        return (s, t) -> {

            s.copyTo(t);

            int oldWidth = s.getIndex().size();
            int expansionWidth = valueProducers.length;

            for (int i = 0; i < expansionWidth; i++) {
                t.set(oldWidth + i, valueProducers[i].map(s));
            }
        };
    }

    void map(RowProxy from, RowBuilder to);

    default RowMapper and(RowMapper m) {
        return (s, t) -> {
            this.map(s, t);
            m.map(s, t);
        };
    }
}
