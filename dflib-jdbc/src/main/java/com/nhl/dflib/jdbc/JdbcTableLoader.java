package com.nhl.dflib.jdbc;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class JdbcTableLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTableLoader.class);

    private JdbcConnector connector;
    private String tableName;

    private String[] columns;
    private Class[] columnTypes;

    public JdbcTableLoader(JdbcConnector connector, String tableName) {
        this.connector = connector;
        this.tableName = tableName;
    }

    public DataFrame load() {
        return new JdbcQueryLoader(connector, this::buildSql).load();
    }

    public JdbcTableLoader includeColumns(String... columns) {
        this.columns = columns;
        return this;
    }

    protected String buildSql(Connection connection) {
        String columns = buildColumnsSql(connection);
        String name = connector.quoteIdentifier(connection, tableName);
        return "select " + columns + " from " + name;
    }

    protected String buildColumnsSql(Connection connection) {

        if (this.columns == null || columns.length == 0) {
            return "*";
        }

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }

            buf.append(connector.quoteIdentifier(connection, columns[i]));
        }

        return buf.toString();
    }
}
