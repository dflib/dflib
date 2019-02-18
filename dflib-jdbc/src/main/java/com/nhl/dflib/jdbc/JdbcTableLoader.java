package com.nhl.dflib.jdbc;

import javax.sql.DataSource;

public class JdbcTableLoader {

    private DataSource dataSource;
    private String tableName;
    private int maxRows;

    public JdbcTableLoader(DataSource dataSource, String tableName) {
        this.dataSource = dataSource;
        this.tableName = tableName;
    }

    public JdbcTableLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

}
