package com.nhl.dflib.jdbc;

import io.bootique.test.junit.BQTestFactory;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class JdbcConnectorIT {

    @ClassRule
    public static BQTestFactory TEST_FACTORY = new BQTestFactory();

    private static DataSource DATA_SOURCE;

    @BeforeClass
    public static void initDataSource() {
        DATA_SOURCE = DbBootstrap.dataSource(TEST_FACTORY, "classpath:com/nhldflib/jdbc/init_schema.sql");
    }

    @Test
    public void testLoadTable() throws SQLException {

        JdbcConnector connector = Jdbc.connector(DATA_SOURCE);

        try (Connection c = connector.getDataSource().getConnection()) {
            assertEquals("Apache Derby", c.getMetaData().getDatabaseProductName());
        }
    }
}
