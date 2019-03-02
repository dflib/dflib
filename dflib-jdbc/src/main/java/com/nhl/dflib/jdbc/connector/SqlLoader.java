package com.nhl.dflib.jdbc.connector;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlLoader extends BaseLoader {

    private String sql;
    private Object[] params;

    public SqlLoader(JdbcConnector connector, String sql) {
        super(connector);
        this.sql = sql;
    }

    public SqlLoader params(Object... params) {
        this.params = params;
        return this;
    }

    public SqlLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    @Override
    protected SqlStatement buildSql(Connection connection) {
        return (params == null || params.length == 0)
                ? new SqlStatementNoParams(sql)
                : new SqlStatementWithParams(sql, params, this::createBinder);
    }

    protected StatementBinder createBinder(PreparedStatement statement) throws SQLException {

        ParameterMetaData pmd = statement.getParameterMetaData();
        int len = pmd.getParameterCount();
        JdbcConsumer<Object>[] bindings = new JdbcConsumer[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = pmd.getParameterType(jdbcPos);
            bindings[i] = connector.getStatementBinder(statement, jdbcType, jdbcPos);
        }

        return new StatementBinder(bindings);
    }
}
