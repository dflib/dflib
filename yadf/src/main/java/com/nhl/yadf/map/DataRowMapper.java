package com.nhl.yadf.map;

@FunctionalInterface
public interface DataRowMapper {

    Object[] map(MapContext c, Object[] row);
}
