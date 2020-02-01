package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.Test;

public class SqlUpdaterIT extends BaseDbTest {

    private JdbcConnector createConnector() {
        return Jdbc.connector(getDataSource());
    }

    @Test
    public void testUpdate() {
        JdbcConnector connector = createConnector();
        connector.sqlUpdater("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (1, 'x', 777.7)")
                .update();

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(1)
                .expectRow(0, 1L, "x", 777.7);
    }

    @Test
    public void testUpdate_Array() {

        JdbcConnector connector = createConnector();
        connector.sqlUpdater("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)")
                .update(1L, "n1", 50_000.01);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(1)
                .expectRow(0, 1L, "n1", 50_000.01);
    }

    @Test
    public void testUpdate_DataFrame() {

        DataFrame data = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = createConnector();
        connector.sqlUpdater("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)")
                .update(data);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testUpdate_ParamWithFunction() {

        DataFrame data = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "na1", 50_000.01,
                2L, "na2", 120_000.);

        JdbcConnector connector = createConnector();
        connector.sqlUpdater("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, SUBSTR(?, 2), ?)")
                .update(data);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, "id", "name", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, "a1", 50_000.01)
                .expectRow(1, 2L, "a2", 120_000.);
    }

    @Test
    public void testSave_Append() {

        DataFrame data1 = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame data2 = DataFrame.newFrame("id", "name", "salary").foldByRow(
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        JdbcConnector connector = createConnector();
        connector.sqlUpdater("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)")
                .update(data1);

        connector.sqlUpdater("insert INTO \"t1\" (\"id\", \"name\", \"salary\") values (?, ?, ?)")
                .update(data2);

        DataFrame saved = connector.tableLoader("t1").load();
        new DataFrameAsserts(saved, columnNames(T1))
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }
}
