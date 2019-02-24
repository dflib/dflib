package com.nhl.dflib.jdbc.load;

import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.connector.JdbcConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableLoader extends BaseLoader {

    private String tableName;
    private String[] columns;

    public TableLoader(JdbcConnector connector, String tableName) {
        super(connector);
        this.tableName = tableName;
    }

    public TableLoader includeColumns(String... columns) {
        this.columns = columns;
        return this;
    }

    // TODO: limit without sorting may return unpredictable data.. should we allow to specify a sort column?
    public TableLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    @Override
    protected String buildSql(Connection connection) {

        // TODO: should maxRows be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..

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

    @Override
    protected Index createIndex(ResultSet rs) throws SQLException {
        return (this.columns == null || columns.length == 0)
                ? super.createIndex(rs)
                : Index.withNames(columns);
    }
}
