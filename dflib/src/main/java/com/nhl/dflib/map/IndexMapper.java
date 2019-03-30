package com.nhl.dflib.map;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface IndexMapper {

    static IndexMapper mapper(int position) {
        return i -> i.getLabel(position);
    }

    static IndexMapper mapper(String name) {
        return i -> name;
    }

    String map(Index i);
}
