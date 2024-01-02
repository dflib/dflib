package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.jdbc.unit.BaseDbTest;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlSaverIT extends BaseDbTest {

    @Test
    public void save() {

        JdbcConnector connector = adapter.createConnector();

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (1, 'x', 777.7)");

        int c = connector.sqlSaver(sql)
                .save();
        assertEquals(1, c);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(1)
                .expectRow(0, 1L, "x", 777.7);
    }

    @Test
    public void save_Array() {


        JdbcConnector connector = adapter.createConnector();

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)");

        int c = connector.sqlSaver(sql)
                .save(1L, "n1", 50_000.01);
        assertEquals(1, c);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(1)
                .expectRow(0, 1L, "n1", 50_000.01);
    }

    @Test
    public void save_DataFrame() {


        DataFrame data = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)");

        JdbcConnector connector = adapter.createConnector();
        int[] cs = connector.sqlSaver(sql)
                .save(data);
        assertArrayEquals(new int[]{1, 1}, cs);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void save_EmptyDataFrame() {

        DataFrame data = DataFrame.empty("id", "name", "salary");

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)");

        JdbcConnector connector = adapter.createConnector();
        int[] cs = connector.sqlSaver(sql)
                .save(data);
        assertArrayEquals(new int[0], cs);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary").expectHeight(0);
    }

    @Test
    public void save_ParamWithFunction() {

        DataFrame data = DataFrame.foldByRow("id", "name", "salary").of(
                1, "na1", 50_000.01,
                2L, "na2", 120_000.);

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, SUBSTR(?, 2), ?)");

        JdbcConnector connector = adapter.createConnector();
        int[] cs = connector.sqlSaver(sql)
                .save(data);
        assertArrayEquals(new int[]{1, 1}, cs);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, "a1", 50_000.01)
                .expectRow(1, 2L, "a2", 120_000.);
    }

    @Test
    public void save_ReuseUpdater() {

        DataFrame data1 = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame data2 = DataFrame.foldByRow("id", "name", "salary").of(
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)");

        JdbcConnector connector = adapter.createConnector();
        SqlSaver saver = connector.sqlSaver(sql);
        int[] cs1 = saver.save(data1);
        int[] cs2 = saver.save(data2);

        assertArrayEquals(new int[]{1, 1}, cs1);
        assertArrayEquals(new int[]{1, 1}, cs2);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, adapter.getColumnNames("t1"))
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void dataTypeConversions() {

        LocalDate ld = LocalDate.of(1977, 02, 05);
        LocalDateTime ldt = LocalDateTime.of(2019, 02, 03, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        Series<Object> data = Series.of(l1, 67, 7.8, 1, "s1", ldt, ld, lt, bytes);
        Series<Object> dataNulls = Series.of(null, null, null, 0, null, null, null, null, null);

        String sql = adapter.toNativeSql("insert into \"t2\" (\"bigint\", \"int\", \"double\", \"boolean\", \"string\", \"timestamp\", \"date\", \"time\", \"bytes\") " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        JdbcConnector connector = adapter.createConnector();

        SqlSaver saver = connector.sqlSaver(sql);
        saver.save(data);
        saver.save(dataNulls);

        DataFrame saved = connector.tableLoader("t2").load();
        new DataFrameAsserts(saved, adapter.getColumnNames("t2"))
                .expectHeight(2)
                .expectRow(0, l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .expectRow(1, null, null, null, false, null, null, null, null, null);
    }

    @Test
    public void save_Update() {

        DataFrame data = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)");

        connector.sqlSaver(sql).save(data);

        DataFrame updateData = DataFrame.foldByRow("name", "id").of(
                "nx", 0,
                "ny", 1);

        sql = adapter.toNativeSql("update \"t1\" set \"name\" = ? where \"id\" > ?");

        int[] cs = connector.sqlSaver(sql).save(updateData);
        assertArrayEquals(new int[]{2, 1}, cs);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, "nx", 50_000.01)
                .expectRow(1, 2L, "ny", 120_000.);
    }
}
