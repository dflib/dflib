package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.Jdbc;
import io.bootique.jdbc.test.derby.DerbyListener;
import io.bootique.log.DefaultBootLogger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class JdbcConnectorBuilder_IT {

    private static final String DERBY_URL = "jdbc:derby:target/derby/testdb1;create=true";

    // TODO: Bootique DerbyListener is not very well suited for managing Derby outside Bootique, so its initialization
    //  and invocations look dirty at times
    private DerbyListener derbyManager = new DerbyListener(new DefaultBootLogger(false));

    @Before
    public void cleanDerby() {
        derbyManager.beforeStartup(null, DERBY_URL);
    }

    @After
    public void stopDerby() {
        derbyManager.afterShutdown(null, DERBY_URL, null);
    }

    @Test
    public void testBuild() throws SQLException {

        // TODO: not really testing username / password as in-memory Derby will take any credentials. Need to use
        //  "testcontainers" and a real DB
        JdbcConnector connector = Jdbc.connector(DERBY_URL).userName("x").password("y").build();
        assertNotNull(connector);

        try (Connection c1 = connector.getConnection()) {
            try (PreparedStatement st = c1.prepareStatement("create table \"x\" (\"id\" int)")) {
               st.executeUpdate();
            }

            c1.commit();
        }

        try (Connection c2 = connector.getConnection()) {
            try (PreparedStatement st = c2.prepareStatement("insert into \"x\" values (356)")) {
                st.executeUpdate();
            }

            c2.commit();
        }

        try (Connection c3 = connector.getConnection()) {
            try (PreparedStatement st = c3.prepareStatement("select * from \"x\"")) {
                try (ResultSet rs = st.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals(356, rs.getInt(1));
                    assertFalse(rs.next());
                }
            }

            c3.commit();
        }
    }
}
