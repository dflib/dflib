package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.jdbc.Jdbc;
import org.dflib.jdbc.connector.loader.JdbcExtractorFactory;
import org.dflib.jdbc.unit.BaseDbTest;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcConnectorBuilder_IT extends BaseDbTest {

    @Test
    @DisplayName("Connector smoke test")
    public void smoke() throws SQLException {

        JdbcConnector connector = Jdbc.connector().dataSource(adapter.getDb().getDataSource()).build();
        String table = "JdbcConnectorBuilder_IT_smoke_test";

        String sql = adapter.toNativeSql("create table \"%s\" (\"id\" int)", table);
        try (Connection c1 = connector.getConnection()) {
            try (PreparedStatement st = c1.prepareStatement(sql)) {
                st.executeUpdate();
            }

            c1.commit();
        }

        sql = adapter.toNativeSql("insert into \"%s\" values (356)", table);
        try (Connection c2 = connector.getConnection()) {
            try (PreparedStatement st = c2.prepareStatement(sql)) {
                st.executeUpdate();
            }

            c2.commit();
        }

        sql = adapter.toNativeSql("select * from \"%s\"", table);
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

    @Test
    @DisplayName("Custom ColumnBuilderFactories")
    public void addColumnBuilderFactory() throws SQLException {

        String table = "JdbcConnectorBuilder_IT_AddColumnBuilderFactory";
        String sql = adapter.toNativeSql("create table \"%s\" (\"id\" bigint, \"d\" date, \"t\" time, \"ts\" timestamp)", table);

        try (Connection c1 = adapter.createConnector().getConnection()) {
            try (PreparedStatement st = c1.prepareStatement(sql)) {
                st.executeUpdate();
            }

            c1.commit();
        }

        LocalDate d = LocalDate.of(1977, 02, 05);
        LocalTime t = LocalTime.of(5, 6, 8);
        LocalDateTime ts = LocalDateTime.of(2019, 02, 03, 1, 2, 5);

        adapter.getTable(table).insertColumns("id", "d", "t", "ts").values(1L, d, t, ts).exec();

        DataFrame customTypes = Jdbc.connector()
                .dataSource(adapter.getDb().getDataSource())
                .addColumnBuilderFactory(Types.DATE, JdbcConnectorBuilder_IT::$cdate)
                .addColumnBuilderFactory(Types.TIME, JdbcConnectorBuilder_IT::$ctime)
                .addColumnBuilderFactory(Types.TIMESTAMP, JdbcConnectorBuilder_IT::$cdatetime)
                .build()
                .tableLoader(table)
                .load();

        new DataFrameAsserts(customTypes, "id", "d", "t", "ts")
                .expectHeight(1)
                .expectRow(0, 1L, d.toString(), t.toString(), ts.toString());

        DataFrame standardTypes = Jdbc.connector()
                .dataSource(adapter.getDb().getDataSource())
                .build()
                .tableLoader(table)
                .load();

        new DataFrameAsserts(standardTypes, "id", "d", "t", "ts")
                .expectHeight(1)
                .expectRow(0, 1L, d, t, ts);
    }

    static Extractor<ResultSet, String> $cdate(int pos) {
        return JdbcExtractorFactory.fromJdbcFunction(rs -> {
            Date date = rs.getDate(pos);
            return date != null ? date.toLocalDate().toString() : null;
        });
    }

    static Extractor<ResultSet, String> $ctime(int pos) {
        return JdbcExtractorFactory.fromJdbcFunction(rs -> {
            Time time = rs.getTime(pos, Calendar.getInstance());
            return time != null ? time.toLocalTime().toString() : null;
        });
    }

    static Extractor<ResultSet, String> $cdatetime(int pos) {
        return JdbcExtractorFactory.fromJdbcFunction(rs -> {
            Timestamp timestamp = rs.getTimestamp(pos, Calendar.getInstance());
            return timestamp != null ? timestamp.toLocalDateTime().toString() : null;
        });
    }

}
