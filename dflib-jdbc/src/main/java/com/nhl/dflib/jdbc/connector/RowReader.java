package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.JdbcFunction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowReader {

    private JdbcFunction<ResultSet, Object>[] valueReaders;

    public RowReader(JdbcFunction<ResultSet, Object>[] valueReaders) {
        this.valueReaders = valueReaders;
    }

    public Object[] readRow(ResultSet resultSet) throws SQLException {

        int width = valueReaders.length;
        Object[] result = new Object[width];

        for (int i = 0; i < width; i++) {
            result[i] = valueReaders[i].apply(resultSet);
        }

        return result;
    }
}
