package com.nhl.dflib.jdbc.load;

import com.nhl.dflib.jdbc.connector.JdbcConnector;

import java.sql.Connection;

public class SqlLoader extends BaseLoader {

    private String sql;
    private Object[] bindings;

    public SqlLoader(JdbcConnector connector, String sql, Object... bindings) {
        super(connector);
        this.sql = sql;
        this.bindings = bindings;
    }

    public SqlLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    @Override
    protected String buildSql(Connection connection) {
        return sql;
    }
}
