package com.nhl.dflib.csv.loader;

import com.nhl.dflib.Index;
import com.nhl.dflib.ValuePredicate;

import java.util.function.Predicate;

public class RowFilterConfig<V> {

    private int columnPosition;
    private String columnName;
    private ValuePredicate<V> condition;

    private RowFilterConfig() {
        columnPosition = -1;
    }

    public static <V> RowFilterConfig<V> create(int pos, ValuePredicate<V> condition) {
        RowFilterConfig<V> c = new RowFilterConfig<>();
        c.columnPosition = pos;
        c.condition = condition;
        return c;
    }

    public static <V> RowFilterConfig<V> create(String name, ValuePredicate<V> condition) {
        RowFilterConfig<V> c = new RowFilterConfig<>();
        c.columnName = name;
        c.condition = condition;
        return c;
    }

    public Predicate<CsvCell<?>[]> toPredicate(Index columns) {
        int pos = columnPosition >= 0 ? columnPosition : columns.position(columnName);
        return vhcs -> condition.test((V) vhcs[pos].get());
    }
}
