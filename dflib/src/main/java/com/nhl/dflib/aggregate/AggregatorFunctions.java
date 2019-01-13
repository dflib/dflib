package com.nhl.dflib.aggregate;

import com.nhl.dflib.map.DataRowToValueMapper;

class AggregatorFunctions {

    static DataRowToValueMapper<String> toString(String column) {
        DataRowToValueMapper<?> reader = DataRowToValueMapper.columnReader(column);
        return (c, r) -> String.valueOf(reader.map(c, r));
    }

    static DataRowToValueMapper<String> toString(int column) {
        DataRowToValueMapper<?> reader = DataRowToValueMapper.columnReader(column);
        return (c, r) -> String.valueOf(reader.map(c, r));
    }
}
