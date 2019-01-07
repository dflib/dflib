package com.nhl.dflib.map;

@FunctionalInterface
public interface DataRowMapper {

    Object[] map(MapContext c, Object[] row);
}
