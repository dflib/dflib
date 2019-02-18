package com.nhl.dflib.jdbc;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class JdbcConnectorIT {

    @ClassRule
    public static MySQLContainer MYSQL = new MySQLContainer();

    @Test
    public void testLoadTable() throws SQLException {
        JdbcConnector connector = Jdbc
                .connector(MYSQL.getJdbcUrl())
                .userName(MYSQL.getUsername())
                .password(MYSQL.getPassword())
                .build();

        try (Connection c = connector.getDataSource().getConnection()) {
            assertEquals("MySQL", c.getMetaData().getDatabaseProductName());
        }
    }
}
