package com.nhl.dflib.jdbc.load;

import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.select.Binding;
import com.nhl.dflib.jdbc.select.StatementBinder;

import java.sql.Connection;

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
    protected StatementBinder createBinder() {
        if(params == null || params.length == 0) {
            return new StatementBinder();
        }

        int len = params.length;
        Binding[] bindings = new Binding[len];
        for(int i = 0; i < len; i++) {
            // TODO: convert java.time objects
            bindings[i] = new Binding(params[i]);
        }

        return new StatementBinder(bindings);
    }
}
