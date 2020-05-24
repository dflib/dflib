package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.jdbc.unit.dbadapter.TestDbAdapter;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlSaverIT extends BaseDbTest {

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSave(TestDbAdapter adapter) {
        deleteTestData(adapter);
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

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSave_Array(TestDbAdapter adapter) {

        deleteTestData(adapter);
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

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSave_DataFrame(TestDbAdapter adapter) {

        deleteTestData(adapter);
        DataFrame data = DataFrame.newFrame("id", "name", "salary").foldByRow(
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

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSave_EmptyDataFrame(TestDbAdapter adapter) {
        deleteTestData(adapter);
        DataFrame data = DataFrame.newFrame("id", "name", "salary").empty();

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)");

        JdbcConnector connector = adapter.createConnector();
        int[] cs = connector.sqlSaver(sql)
                .save(data);
        assertArrayEquals(new int[0], cs);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary").expectHeight(0);
    }

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSave_ParamWithFunction(TestDbAdapter adapter) {
        deleteTestData(adapter);
        DataFrame data = DataFrame.newFrame("id", "name", "salary").foldByRow(
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

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSave_ReuseUpdater(TestDbAdapter adapter) {
        deleteTestData(adapter);
        DataFrame data1 = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame data2 = DataFrame.newFrame("id", "name", "salary").foldByRow(
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

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testDataTypeConversions(TestDbAdapter adapter) {
        deleteTestData(adapter);
        LocalDate ld = LocalDate.of(1977, 02, 05);
        LocalDateTime ldt = LocalDateTime.of(2019, 02, 03, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        Series<Object> data = Series.forData(l1, 67, 7.8, 1, "s1", ldt, ld, lt, bytes);
        Series<Object> dataNulls = Series.forData(null, null, null, 0, null, null, null, null, null);

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

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSave_Update(TestDbAdapter adapter) {
        deleteTestData(adapter);
        DataFrame data = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();

        String sql = adapter.toNativeSql("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)");

        connector.sqlSaver(sql).save(data);

        DataFrame updateData = DataFrame.newFrame("name", "id").foldByRow(
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
