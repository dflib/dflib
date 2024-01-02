package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.jdbc.SaveOp;
import org.dflib.jdbc.unit.BaseDbTest;
import org.dflib.junit5.DataFrameAsserts;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.*;

public class TableSaverIT extends BaseDbTest {

    @Test
    public void test() {

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t1").save(df);

        assertT1Contents()
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void empty() {

        DataFrame df = DataFrame.empty("id", "name", "salary");

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t1").save(df);

        assertT1Contents().expectHeight(0);
    }

    @Test
    public void save_Append() {

        DataFrame df1 = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.foldByRow("id", "name", "salary").of(
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        JdbcConnector connector = adapter.createConnector();
        TableSaver saver = connector.tableSaver("t1");
        saver.save(df1);
        saver.save(df2);

        assertT1Contents()
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void save_DeleteTableData() {

        DataFrame df1 = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.foldByRow("id", "name", "salary").of(
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        JdbcConnector connector = adapter.createConnector();
        TableSaver saver = connector
                .tableSaver("t1")
                .deleteTableData();
        saver.save(df1);
        saver.save(df2);

        assertT1Contents()
                .expectHeight(2)
                .expectRow(0, 3L, "n3", 60_000.01)
                .expectRow(1, 4L, "n4", 20_000.);
    }

    @Test
    public void save_MergeByPk() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1_x", 50_000.02,
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        JdbcConnector connector = adapter.createConnector();
        connector
                .tableSaver("t1")
                .mergeByPk()
                .save(df);

        assertT1Contents()
                .expectHeight(4)
                .expectRow(0, 1L, "n1_x", 50_000.02)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void save_MergeByColumns() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.02,
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        adapter.getTable("t1_audit").deleteAll();

        JdbcConnector connector = adapter.createConnector();
        connector
                .tableSaver("t1")
                .mergeByColumns("name", "id")
                .save(df);

        adapter.getTable("t1_audit").matcher().eq("op", "INSERT").assertMatches(2);
        adapter.getTable("t1_audit").matcher().eq("op", "UPDATE").assertMatches(1);

        assertT1Contents()
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.02)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void save_MergeByColumns_DeleteUnmatchedRows() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.02,
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        adapter.getTable("t1_audit").deleteAll();

        JdbcConnector connector = adapter.createConnector();
        connector
                .tableSaver("t1")
                .mergeByColumns("name", "id")
                .deleteUnmatchedRows()
                .save(df);

        adapter.getTable("t1_audit").matcher().eq("op", "INSERT").assertMatches(2);
        adapter.getTable("t1_audit").matcher().eq("op", "UPDATE").assertMatches(1);
        adapter.getTable("t1_audit").matcher().eq("op", "DELETE").assertMatches(1);

        assertT1Contents()
                .expectHeight(3)
                .expectRow(0, 1L, "n1", 50_000.02)
                .expectRow(1, 3L, "n3", 60_000.01)
                .expectRow(2, 4L, "n4", 20_000.);
    }

    @Test
    public void save_SkipUpdatingUnchagedRows() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1_x", 50_000.02,
                2L, "n2", 120_000.,
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        adapter.getTable("t1_audit").deleteAll();

        JdbcConnector connector = adapter.createConnector();
        connector
                .tableSaver("t1")
                .mergeByPk()
                .save(df);

        adapter.getTable("t1_audit").matcher().eq("op", "INSERT").assertMatches(2);
        adapter.getTable("t1_audit").matcher().eq("op", "UPDATE").assertMatches(1);

        assertT1Contents()
                .expectHeight(4)
                .expectRow(0, 1L, "n1_x", 50_000.02)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void save_SkipUpdatingUnchagedColumns() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(4L, "n4", 8.)
                .values(1L, "n1", 5.)
                .values(2L, "n2", 6.)
                .values(5L, "n5", 9.)
                .values(3L, "n3", 7.)
                .exec();

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1_x", 5.,
                2L, "n2", 6.01,
                3L, "n3", 7.01,
                4L, "n4", 8.,
                5L, "n5_x", 9.01);

        adapter.getTable("t1_audit").deleteAll();
        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t1").mergeByPk().save(df);

        adapter.getTable("t1_audit").matcher().eq("op", "INSERT").assertMatches(0);

        // counting updated rows, not update statements
        // TODO: how do we test how the update was partitioned into statements?
        adapter.getTable("t1_audit").matcher().eq("op", "UPDATE").assertMatches(4);

        assertT1Contents()
                .expectHeight(5)
                .expectRow(0, 1L, "n1_x", 5.)
                .expectRow(1, 2L, "n2", 6.01)
                .expectRow(2, 3L, "n3", 7.01)
                .expectRow(3, 4L, "n4", 8.)
                .expectRow(4, 5L, "n5_x", 9.01);
    }

    @Test
    public void dataTypes() {

        LocalDate ld = LocalDate.of(1977, 2, 5);
        LocalDateTime ldt = LocalDateTime.of(2019, 2, 3, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        DataFrame df = DataFrame.foldByRow("bigint", "int", "timestamp", "time", "date", "bytes")
                .of(l1, 1, ldt, lt, ld, bytes);

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int", "timestamp", "time", "date", "bytes")
                .load()
                .sort("bigint", true);

        new DataFrameAsserts(df2, df.getColumnsIndex())
                .expectHeight(1)
                .expectRow(0, l1, 1, ldt, lt, ld, bytes);
    }

    @Test
    public void dataTypes_DatePartsAsInts() {

        DataFrame df = DataFrame.foldByRow("bigint", "int").of(
                1L, Month.DECEMBER,
                2L, Year.of(1973),
                3L, DayOfWeek.TUESDAY);

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int")
                .load()
                .sort("bigint", true);

        new DataFrameAsserts(df2, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 1L, 12)
                .expectRow(1, 2L, 1973)
                .expectRow(2, 3L, 2);
    }

    @Test
    public void dataTypes_Enums() {

        DataFrame df = DataFrame.foldByRow("bigint", "int", "string").of(
                1L, X.a, X.a,
                2L, X.b, X.b);

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int", "string")
                .load()
                .sort("bigint", true);

        new DataFrameAsserts(df2, df.getColumnsIndex())
                .expectHeight(2)
                .expectRow(0, 1L, 0, "a")
                .expectRow(1, 2L, 1, "b");
    }

    @Test
    public void saveWithInfo_Insert() {

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        SaveStats info = connector
                .tableSaver("t1")
                .save(df);

        new SeriesAsserts(info.getRowSaveStatuses()).expectData(SaveOp.insert, SaveOp.insert);

        assertT1Contents()
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void saveWithInfo_DeleteInsert() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        SaveStats info = connector
                .tableSaver("t1")
                .deleteTableData()
                .save(df);

        new SeriesAsserts(info.getRowSaveStatuses()).expectData(SaveOp.insert, SaveOp.insert);

        assertT1Contents()
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void save_MergeByPk_DeleteUnmatchedRows() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                2L, "n2", 120_001.,
                3L, "n3", 11_000.);

        JdbcConnector connector = adapter.createConnector();
        SaveStats info = connector
                .tableSaver("t1")
                .mergeByPk()
                .deleteUnmatchedRows()
                .save(df);

        new SeriesAsserts(info.getRowSaveStatuses()).expectData(SaveOp.update, SaveOp.insert);

        assertT1Contents()
                .expectHeight(2)
                .expectRow(0, 2L, "n2", 120_001.)
                .expectRow(1, 3L, "n3", 11_000.);
    }

    @Test
    public void saveWithInfo_Merge() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .values(4L, "n4", 4.)
                .values(5L, "n5", 5.)
                .exec();

        DataFrame df = DataFrame.foldByRow("id", "name", "salary").of(
                1L, "n1", 50_000.01,
                2L, "n2_u", 120_000.,
                3L, "n3", 320_000.,
                4L, "n4_u", 4.,
                5L, "n5", 5.);

        JdbcConnector connector = adapter.createConnector();
        SaveStats info = connector
                .tableSaver("t1")
                .mergeByPk()
                .save(df);

        new SeriesAsserts(info.getRowSaveStatuses()).expectData(SaveOp.skip, SaveOp.update, SaveOp.insert, SaveOp.update, SaveOp.skip);

        assertT1Contents()
                .expectHeight(5)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2_u", 120_000.)
                .expectRow(2, 3L, "n3", 320_000.)
                .expectRow(3, 4L, "n4_u", 4.)
                .expectRow(4, 5L, "n5", 5.);
    }

    private DataFrameAsserts assertT1Contents() {
        DataFrame df = adapter.createConnector().tableLoader("t1").load().sort("id", true);
        return new DataFrameAsserts(df, adapter.getColumnNames("t1"));
    }

    enum X {a, b}
}
