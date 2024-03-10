package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.jdbc.unit.BaseDbTest;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SqlLoaderIT extends BaseDbTest {

    @Test
    public void test() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 1_000.);

        String sql = adapter.toNativeSql("SELECT \"id\", \"salary\" from \"t1\" WHERE \"id\" > 1");

        DataFrame df = adapter.createConnector()
                .sqlLoader(sql)
                .load();

        new DataFrameAsserts(df, "id", "salary")
                .expectHeight(2)
                .expectRow(0, 2L, 120_000.)
                .expectRow(1, 3L, 1_000.);
    }

    @Test
    public void reuse() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 1_000.);

        String sql = adapter.toNativeSql("SELECT \"id\", \"salary\" from \"t1\" WHERE \"id\" = ?");

        SqlLoader loader = adapter.createConnector().sqlLoader(sql);

        DataFrame df1 = loader.load(2L);
        new DataFrameAsserts(df1, "id", "salary")
                .expectHeight(1)
                .expectRow(0, 2L, 120_000.);

        DataFrame df2 = loader.load(1L);
        new DataFrameAsserts(df2, "id", "salary")
                .expectHeight(1)
                .expectRow(0, 1L, 50_000.01);
    }

    @Test
    public void empty() {

        adapter.getTable("t1").insert(1L, "n1", 50_000.01);

        String sql = adapter.toNativeSql("SELECT \"id\", \"salary\" from \"t1\" WHERE \"id\" > 1");

        DataFrame df = adapter
                .createConnector()
                .sqlLoader(sql)
                .load();

        new DataFrameAsserts(df, "id", "salary").expectHeight(0);
    }

    @Test
    public void columnFunctions() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 1_000.);

        String sql = adapter.toNativeSql("SELECT SUBSTR(\"name\", 2) as \"name\" from \"t1\" WHERE \"id\" > 1");

        DataFrame df = adapter.createConnector()
                .sqlLoader(sql)
                .load();

        new DataFrameAsserts(df, "name")
                .expectHeight(2)
                .expectRow(0, "2")
                .expectRow(1, "3");
    }

    @Test
    public void limit() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 20_000.);

        String sql = adapter.toNativeSql("SELECT * from \"t1\"");

        DataFrame df = adapter.createConnector()
                .sqlLoader(sql)
                .limit(2)
                .load();

        new DataFrameAsserts(df, adapter.getColumnNames("t1"))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void params() {

        LocalDate ld = LocalDate.of(1977, 02, 05);
        LocalDateTime ldt = LocalDateTime.of(2019, 02, 03, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        adapter.getTable("t2").insert(l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .insert(null, null, null, false, null, null, null, null, null);

        String sql = adapter.toNativeSql("SELECT * from \"t2\"" +
                " WHERE \"bigint\" = ?" +
                " AND \"int\" = ?" +
                " AND \"double\" = ?" +
                " AND \"boolean\" = ?" +
                " AND \"string\" = ?" +
                " AND \"timestamp\" = ?" +
                " AND \"date\" = ?" +
                " AND \"time\" = ?" +
                " AND \"bytes\" = ?");

        DataFrame df = adapter.createConnector()
                .sqlLoader(sql)
                .load(l1, 67, 7.8, 1, "s1", ldt, ld, lt, bytes);

        new DataFrameAsserts(df, adapter.getColumnNames("t2"))
                .expectHeight(1)
                .expectRow(0, l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes);
    }

    @Test
    public void primitives() {


        adapter.getTable("t3").insert(-15, Long.MAX_VALUE - 1, 0.505, true);

        String sql = adapter.toNativeSql("SELECT * from \"t3\"");

        DataFrame df = adapter.createConnector()
                .sqlLoader(sql)
                .load();

        new DataFrameAsserts(df, "int", "long", "double", "boolean")
                .expectHeight(1)
                .expectIntColumns(0)
                .expectLongColumns(1)
                .expectDoubleColumns(2)
                .expectBooleanColumns("boolean")
                .expectRow(0, -15, Long.MAX_VALUE - 1, 0.505, true);
    }
}
