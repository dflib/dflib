package com.nhl.dflib.jdbc.connector;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowReader<T> {

    T readRow(ResultSet rs) throws SQLException;

    static RowReader<Object[]> arrayReader(int width) {
        return rs -> {
            Object[] result = new Object[width];

            for (int i = 1; i <= width; i++) {
                result[i - 1] = rs.getObject(i);
            }

            return result;
        };
    }
}
