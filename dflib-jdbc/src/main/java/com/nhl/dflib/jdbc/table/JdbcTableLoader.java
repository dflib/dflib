package com.nhl.dflib.jdbc.table;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.JdbcOperation;
import com.nhl.dflib.jdbc.select.JdbcSelector;
import com.nhl.dflib.jdbc.select.RowReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JdbcTableLoader {

    private JdbcConnector connector;
    private String tableName;
    private String[] columns;
    private int maxRows;

    public JdbcTableLoader(JdbcConnector connector, String tableName) {
        this.connector = connector;
        this.tableName = tableName;
        this.maxRows = Integer.MAX_VALUE;
    }

    public DataFrame load() {

        // TODO: should maxRows be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..
        return new JdbcSelector(
                connector,
                this::buildSql,
                this::createIndex,
                this::createRowReader,
                maxRows).load();
    }

    public JdbcTableLoader includeColumns(String... columns) {
        this.columns = columns;
        return this;
    }

    // TODO: limit without sorting may return unpredictable data.. should we allow to specify a sort column?
    public JdbcTableLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
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

    protected Index createIndex(ResultSet rs) throws SQLException {

        if (this.columns == null || columns.length == 0) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int width = rsmd.getColumnCount();
            String[] names = new String[width];

            for (int i = 0; i < width; i++) {
                names[i] = rsmd.getColumnLabel(i + 1);
            }

            return Index.withNames(names);
        } else {
            return Index.withNames(columns);
        }
    }

    protected RowReader createRowReader(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int width = rsmd.getColumnCount();
        JdbcOperation<ResultSet, Object>[] readers = new JdbcOperation[width];

        for (int i = 0; i < width; i++) {
            int jdbcPos = i + 1;
            readers[i] = connector.getValueReader(rsmd.getColumnType(jdbcPos), jdbcPos);
        }

        return new RowReader(readers);
    }
}
