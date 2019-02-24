package com.nhl.dflib.jdbc.select;

import com.nhl.dflib.jdbc.connector.JdbcOperation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowReader {

    private JdbcOperation<ResultSet, Object>[] valueReaders;

    public RowReader(JdbcOperation<ResultSet, Object>[] valueReaders) {
        this.valueReaders = valueReaders;
    }

    public Object[] readRow(ResultSet resultSet) throws SQLException {

        int width = valueReaders.length;
        Object[] result = new Object[width];

        for (int i = 0; i < width; i++) {
            result[i] = valueReaders[i].exec(resultSet);
        }

        return result;
    }
}
