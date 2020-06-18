package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableDeleterIT extends BaseDbTest {

    @Test
    public void testDelete_All() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        int deleted = connector.tableDeleter("t1").delete();
        assertEquals(2, deleted);

        DataFrame df = connector.tableLoader("t1").load();
        new DataFrameAsserts(df, adapter.getColumnNames("t1")).expectHeight(0);
    }

    @Test
    public void testDelete_Eq() {

        DataFrame matcher = DataFrame.newFrame("id").foldByRow(1L, 3L);

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        int deleted = connector.tableDeleter("t1")
                .eq(matcher)
                .delete();
        assertEquals(1, deleted);

        DataFrame df = connector.tableLoader("t1").load();
        new DataFrameAsserts(df, adapter.getColumnNames("t1"))
                .expectHeight(1)
                .expectRow(0, 2L, "n2", 120_000.);
    }

    @Test
    public void testDelete_Eq_Empty() {

        DataFrame matcher = DataFrame.newFrame("id").empty();

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        int deleted = connector.tableDeleter("t1")
                .eq(matcher)
                .delete();
        assertEquals(0, deleted);

        DataFrame df = connector.tableLoader("t1").load();
        new DataFrameAsserts(df, adapter.getColumnNames("t1"))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testDelete_Neq() {

        DataFrame matcher = DataFrame.newFrame("id").foldByRow(1L, 3L);

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        int deleted = connector.tableDeleter("t1")
                .neq(matcher)
                .delete();
        assertEquals(1, deleted);

        DataFrame df = connector.tableLoader("t1").load();
        new DataFrameAsserts(df, adapter.getColumnNames("t1"))
                .expectHeight(1)
                .expectRow(0, 1L, "n1", 50_000.01);
    }

    @Test
    public void testDelete_Neq_Empty() {

        DataFrame matcher = DataFrame.newFrame("id").empty();

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        int deleted = connector.tableDeleter("t1")
                .neq(matcher)
                .delete();
        assertEquals(2, deleted);

        DataFrame df = connector.tableLoader("t1").load();
        new DataFrameAsserts(df, adapter.getColumnNames("t1")).expectHeight(0);
    }
}
