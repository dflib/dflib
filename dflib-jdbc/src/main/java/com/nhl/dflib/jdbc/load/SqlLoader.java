package com.nhl.dflib.jdbc.load;

import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.JdbcConsumer;
import com.nhl.dflib.jdbc.select.StatementBinder;

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
    protected String buildSql(Connection connection) {
        return sql;
    }

    @Override
    protected StatementBinder createBinder(PreparedStatement statement) throws SQLException {
        if (params == null || params.length == 0) {
            return new StatementBinder();
        }

        int len = params.length;
        JdbcConsumer<PreparedStatement>[] bindings = new JdbcConsumer[len];
        ParameterMetaData pmd = statement.getParameterMetaData();

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = pmd.getParameterType(jdbcPos);
            bindings[i] = connector.getStatementBinder(jdbcType, jdbcPos, params[i]);
        }

        return new StatementBinder(bindings);

    }
}
