package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class JdbcConnectorBuilder_IT extends BaseDbTest {

    private JdbcConnector createConnector() {
        return Jdbc.connector(getDataSource());
    }

    @Test
    public void testBuild() throws SQLException {

        JdbcConnector connector = createConnector();

        String sql = toNativeSql("create table \"x\" (\"id\" int)");

        try (Connection c1 = connector.getConnection()) {
            try (PreparedStatement st = c1.prepareStatement(sql)) {
               st.executeUpdate();
            }

            c1.commit();
        }

        sql = toNativeSql("insert into \"x\" values (356)");

        try (Connection c2 = connector.getConnection()) {
            try (PreparedStatement st = c2.prepareStatement(sql)) {
                st.executeUpdate();
            }

            c2.commit();
        }

        sql = toNativeSql("select * from \"x\"");

        try (Connection c3 = connector.getConnection()) {
            try (PreparedStatement st = c3.prepareStatement(sql)) {
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
