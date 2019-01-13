package com.nhl.dflib.map;

import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;

@FunctionalInterface
public interface IndexMapper {

    static IndexMapper mapper(int position) {
        return i -> i.getPositions()[position];
    }

    static IndexMapper mapper(String name) {
        return i -> i.position(name);
    }

    IndexPosition map(Index i);
}
