package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.unit.DFAsserts;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

public class JdbcConnector_TableSaverIT extends BaseDbTest {

    private JdbcConnector connector;

    @Before
    public void createConnector() {
        this.connector = Jdbc.connector(getDataSource());
    }

    @Test
    public void test() {

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        connector
                .tableSaver("t1")
                .save(df);

        DataFrame df2 = connector
                .tableLoader("t1")
                .load();

        new DFAsserts(df2, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testEmpty() {

        DataFrame df = DataFrame.forSequenceFoldByRow(Index.forLabels("id", "name", "salary"));

        connector.tableSaver("t1").save(df);

        DataFrame df2 = connector
                .tableLoader("t1")
                .load();

        new DFAsserts(df2, columnNames(T1)).expectHeight(0);
    }

    @Test
    public void testSave_Append() {

        DataFrame df1 = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        TableSaver saver = connector.tableSaver("t1");
        saver.save(df1);
        saver.save(df2);

        DataFrame df3 = connector.tableLoader("t1").load();

        new DFAsserts(df3, columnNames(T1))
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void testSave_DeleteTableData() {

        DataFrame df1 = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);


        TableSaver saver = connector
                .tableSaver("t1")
                .deleteTableData();
        saver.save(df1);
        saver.save(df2);

        DataFrame df3 = connector
                .tableLoader("t1")
                .load();

        new DFAsserts(df3, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 3L, "n3", 60_000.01)
                .expectRow(1, 4L, "n4", 20_000.);
    }

    @Deprecated
    @Test
    public void testSave_StoreRowNumber() {

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("name", "salary"),
                "n1", 50_000.01,
                "n2", 120_000.);

        connector
                .tableSaver("t1")
                .storeRowNumber("id")
                .save(df);

        DataFrame df2 = connector
                .tableLoader("t1")
                .load();

        new DFAsserts(df2, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testSave_MergeByPk() {

        T1.insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                1L, "n1_x", 50_000.02,
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        connector
                .tableSaver("t1")
                .mergeByPk()
                .save(df);

        DataFrame df3 = connector
                .tableLoader("t1")
                .load();

        new DFAsserts(df3, columnNames(T1))
                .expectHeight(4)
                .expectRow(0, 1L, "n1_x", 50_000.02)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void testSave_MergeByColumns() {

        T1.insertColumns("id", "name", "salary")
                .values(1L, "n1", 50_000.01)
                .values(2L, "n2", 120_000.)
                .exec();

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                1L, "n1", 50_000.02,
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        connector
                .tableSaver("t1")
                .mergeByColumns("name", "id")
                .save(df);

        DataFrame df3 = connector
                .tableLoader("t1")
                .load();

        new DFAsserts(df3, columnNames(T1))
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.02)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void testDataTypes() {

        LocalDate ld = LocalDate.of(1977, 02, 05);
        LocalDateTime ldt = LocalDateTime.of(2019, 02, 03, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("bigint", "int", "timestamp", "time", "date", "bytes"),
                l1, 1, ldt, lt, ld, bytes);

        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int", "timestamp", "time", "date", "bytes")
                .load();

        new DFAsserts(df2, df.getColumnsIndex())
                .expectHeight(1)
                .expectRow(0, l1, 1, ldt, lt, ld, bytes);
    }

    @Test
    public void testDataTypes_DatePartsAsInts() {

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("bigint", "int"),
                1L, Month.DECEMBER,
                2L, Year.of(1973),
                3L, DayOfWeek.TUESDAY);

        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int")
                .load();

        new DFAsserts(df2, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 1L, 12)
                .expectRow(1, 2L, 1973)
                .expectRow(2, 3L, 2);
    }

    @Test
    public void testDataTypes_Enums() {

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("bigint", "int", "string"),
                1L, X.a, X.a,
                2L, X.b, X.b);

        connector.tableSaver("t2").save(df);

        DataFrame df2 = connector
                .tableLoader("t2")
                .includeColumns("bigint", "int", "string")
                .load();

        new DFAsserts(df2, df.getColumnsIndex())
                .expectHeight(2)
                .expectRow(0, 1L, 0, "a")
                .expectRow(1, 2L, 1, "b");
    }

    enum X {a, b}
}
