package com.nhl.dflib.jdbc;

import javax.sql.DataSource;

public class JdbcConnector {

    private DataSource dataSource;

    public JdbcConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JdbcTableLoader readTable(String name) {
        return new JdbcTableLoader(dataSource);
    }
}
