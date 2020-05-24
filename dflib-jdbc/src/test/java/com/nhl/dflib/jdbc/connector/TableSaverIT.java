package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.SaveOp;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.unit.DataFrameAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.*;

public class TableSaverIT extends BaseDbTest {

    @Test
    public void test() {

        DataFrame df = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t1").save(df);
        DataFrame df2 = connector.tableLoader("t1").load();

        new DataFrameAsserts(df2, adapter.getColumnNames("t1"))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testEmpty() {

        DataFrame df = DataFrame.newFrame("id", "name", "salary").empty();

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t1").save(df);
        DataFrame df2 = connector
                .tableLoader("t1")
                .load();

        new DataFrameAsserts(df2, adapter.getColumnNames("t1")).expectHeight(0);
    }

    @Test
    public void testSave_Append() {

        DataFrame df1 = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.newFrame("id", "name", "salary").foldByRow(
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        JdbcConnector connector = adapter.createConnector();
        TableSaver saver = connector.tableSaver("t1");
        saver.save(df1);
        saver.save(df2);

        DataFrame df3 = connector.tableLoader("t1").load();

        new DataFrameAsserts(df3, adapter.getColumnNames("t1"))
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void testSave_DeleteTableData() {

        DataFrame df1 = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.newFrame("id", "name", "salary").foldByRow(
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        JdbcConnector connector = adapter.createConnector();
        TableSaver saver = connector
                .tableSaver("t1")
                .deleteTableData();
        saver.save(df1);
        saver.save(df2);

        DataFrame df3 = connector
                .tableLoader("t1")
                .load();

        new DataFrameAsserts(df3, adapter.getColumnNames("t1"))
                .expectHeight(2)
                .expectRow(0, 3L, "n3", 60_000.01)
                .expectRow(1, 4L, "n4", 20_000.);
    }

    @Test
    public void testSave_MergeByPk() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1_x", 50_000.02,
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        JdbcConnector connector = adapter.createConnector();
        connector
                .tableSaver("t1")
                .mergeByPk()
                .save(df);

        DataFrame df3 = connector
                .tableLoader("t1")
                .load()
                .sort(0, true);

        new DataFrameAsserts(df3, adapter.getColumnNames("t1"))
                .expectHeight(4)
                .expectRow(0, 1L, "n1_x", 50_000.02)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void testSave_MergeByColumns() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.newFrame("id", "name", "salary").foldByRow(
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

        DataFrame df3 = connector
                .tableLoader("t1")
                .load()
                .sort(0, true);

        new DataFrameAsserts(df3, adapter.getColumnNames("t1"))
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.02)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void testSave_SkipUpdatingUnchagedRows() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.newFrame("id", "name", "salary").foldByRow(
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

        DataFrame df3 = connector
                .tableLoader("t1")
                .load()
                .sort(0, true);

        new DataFrameAsserts(df3, adapter.getColumnNames("t1"))
                .expectHeight(4)
                .expectRow(0, 1L, "n1_x", 50_000.02)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void testSave_SkipUpdatingUnchagedColumns() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(4L, "n4", 8.)
                .values(1L, "n1", 5.)
                .values(2L, "n2", 6.)
                .values(5L, "n5", 9.)
                .values(3L, "n3", 7.)
                .exec();

        DataFrame df = DataFrame.newFrame("id", "name", "salary").foldByRow(
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

        DataFrame df3 = connector
                .tableLoader("t1")
                .load().sort("id", true);

        new DataFrameAsserts(df3, adapter.getColumnNames("t1"))
                .expectHeight(5)
                .expectRow(0, 1L, "n1_x", 5.)
                .expectRow(1, 2L, "n2", 6.01)
                .expectRow(2, 3L, "n3", 7.01)
                .expectRow(3, 4L, "n4", 8.)
                .expectRow(4, 5L, "n5_x", 9.01);
    }

    @Test
    public void testDataTypes() {

        LocalDate ld = LocalDate.of(1977, 2, 5);
        LocalDateTime ldt = LocalDateTime.of(2019, 2, 3, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        DataFrame df = DataFrame.newFrame("bigint", "int", "timestamp", "time", "date", "bytes")
                .foldByRow(l1, 1, ldt, lt, ld, bytes);

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int", "timestamp", "time", "date", "bytes")
                .load();

        new DataFrameAsserts(df2, df.getColumnsIndex())
                .expectHeight(1)
                .expectRow(0, l1, 1, ldt, lt, ld, bytes);
    }

    @Test
    public void testDataTypes_DatePartsAsInts() {

        DataFrame df = DataFrame.newFrame("bigint", "int").foldByRow(
                1L, Month.DECEMBER,
                2L, Year.of(1973),
                3L, DayOfWeek.TUESDAY);

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int")
                .load();

        new DataFrameAsserts(df2, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 1L, 12)
                .expectRow(1, 2L, 1973)
                .expectRow(2, 3L, 2);
    }

    @Test
    public void testDataTypes_Enums() {

        DataFrame df = DataFrame.newFrame("bigint", "int", "string").foldByRow(
                1L, X.a, X.a,
                2L, X.b, X.b);

        JdbcConnector connector = adapter.createConnector();
        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int", "string")
                .load();

        new DataFrameAsserts(df2, df.getColumnsIndex())
                .expectHeight(2)
                .expectRow(0, 1L, 0, "a")
                .expectRow(1, 2L, 1, "b");
    }

    @Test
    public void testSaveWithInfo_Insert() {

        DataFrame df = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        SaveStats info = connector
                .tableSaver("t1")
                .save(df);

        new SeriesAsserts(info.getRowSaveStatuses()).expectData(SaveOp.insert, SaveOp.insert);

        DataFrame dfSaved = connector.tableLoader("t1").load();
        new DataFrameAsserts(dfSaved, adapter.getColumnNames("t1"))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testSaveWithInfo_DeleteInsert() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.newFrame("id", "name", "salary").foldByRow(
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        JdbcConnector connector = adapter.createConnector();
        SaveStats info = connector
                .tableSaver("t1")
                .deleteTableData()
                .save(df);

        new SeriesAsserts(info.getRowSaveStatuses()).expectData(SaveOp.insert, SaveOp.insert);

        DataFrame dfSaved = connector.tableLoader("t1").load();
        new DataFrameAsserts(dfSaved, adapter.getColumnNames("t1"))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testSaveWithInfo_Merge() {

        adapter.getTable("t1").insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .values(4L, "n4", 4.)
                .values(5L, "n5", 5.)
                .exec();

        DataFrame df = DataFrame.newFrame("id", "name", "salary").foldByRow(
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

        DataFrame dfSaved = connector.tableLoader("t1").load().sort("id", true);
        new DataFrameAsserts(dfSaved, adapter.getColumnNames("t1"))
                .expectHeight(5)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2_u", 120_000.)
                .expectRow(2, 3L, "n3", 320_000.)
                .expectRow(3, 4L, "n4_u", 4.)
                .expectRow(4, 5L, "n5", 5.);
    }

    enum X {a, b}
}
