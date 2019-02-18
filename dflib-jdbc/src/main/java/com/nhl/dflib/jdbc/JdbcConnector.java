package com.nhl.dflib.jdbc;

import javax.sql.DataSource;

public class JdbcConnector {

    private DataSource dataSource;

    public JdbcConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JdbcTableLoader tableLoader(String tableName) {
        return new JdbcTableLoader(dataSource, tableName);
    }
}
