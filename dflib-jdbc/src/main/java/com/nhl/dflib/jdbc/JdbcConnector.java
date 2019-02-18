package com.nhl.dflib.jdbc;

import javax.sql.DataSource;

public class JdbcConnector {

    private DataSource dataSource;

    public JdbcConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JdbcTableLoader readTable(String tableName) {
        return new JdbcTableLoader(dataSource, tableName);
    }
}
