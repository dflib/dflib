package com.nhl.dflib.map;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface ColumnLocator {

    static ColumnLocator forPosition(int position) {
        return i -> position;
    }

    static ColumnLocator forName(String name) {
        return i -> i.position(name);
    }

    int position(Index i);
}
