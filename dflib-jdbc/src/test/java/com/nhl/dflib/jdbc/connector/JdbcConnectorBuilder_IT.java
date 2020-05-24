package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcConnectorBuilder_IT extends BaseDbTest {

    @Test
    public void testBuild() throws SQLException {

        JdbcConnector connector = adapter.createConnector();

        String sql = adapter.toNativeSql("create table \"JdbcConnectorBuilder_IT\" (\"id\" int)");

        try (Connection c1 = connector.getConnection()) {
            try (PreparedStatement st = c1.prepareStatement(sql)) {
               st.executeUpdate();
            }

            c1.commit();
        }

        sql = adapter.toNativeSql("insert into \"JdbcConnectorBuilder_IT\" values (356)");

        try (Connection c2 = connector.getConnection()) {
            try (PreparedStatement st = c2.prepareStatement(sql)) {
                st.executeUpdate();
            }

            c2.commit();
        }

        sql = adapter.toNativeSql("select * from \"JdbcConnectorBuilder_IT\"");

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
