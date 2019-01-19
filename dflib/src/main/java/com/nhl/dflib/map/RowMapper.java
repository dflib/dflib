package com.nhl.dflib.map;

@FunctionalInterface
public interface RowMapper {

    Object[] map(MapContext c, Object[] row);
}
