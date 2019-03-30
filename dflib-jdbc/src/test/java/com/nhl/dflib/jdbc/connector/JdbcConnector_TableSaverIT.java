package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.unit.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.Test;

public class JdbcConnector_TableSaverIT extends BaseDbTest {

    private JdbcConnector createConnector() {
        return Jdbc.connector(getDataSource());
    }

    @Test
    public void test() {

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        createConnector()
                .tableSaver("t1")
                .save(df);

        DataFrame df2 = createConnector()
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

        createConnector()
                .tableSaver("t1")
                .save(df);

        DataFrame df2 = createConnector()
                .tableLoader("t1")
                .load();

        new DFAsserts(df2, columnNames(T1)).expectHeight(0);
    }

    @Test
    public void testAppend() {

        DataFrame df1 = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);

        createConnector()
                .tableSaver("t1")
                .save(df1)
                .save(df2);

        DataFrame df3 = createConnector()
                .tableLoader("t1")
                .load();

        new DFAsserts(df3, columnNames(T1))
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 20_000.);
    }

    @Test
    public void testDeleteTableData() {

        DataFrame df1 = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                1L, "n1", 50_000.01,
                2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.forSequenceFoldByRow(
                Index.forLabels("id", "name", "salary"),
                3L, "n3", 60_000.01,
                4L, "n4", 20_000.);


        createConnector()
                .tableSaver("t1")
                .deleteTableData()
                .save(df1)
                .save(df2);

        DataFrame df3 = createConnector()
                .tableLoader("t1")
                .load();

        new DFAsserts(df3, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 3L, "n3", 60_000.01)
                .expectRow(1, 4L, "n4", 20_000.);
    }

    @Test
    public void testStoreRowNumber() {

        DataFrame df = DataFrame.forSequenceFoldByRow(
                Index.forLabels("name", "salary"),
                "n1", 50_000.01,
                "n2", 120_000.);

        createConnector()
                .tableSaver("t1")
                .storeRowNumber("id")
                .save(df);

        DataFrame df2 = createConnector()
                .tableLoader("t1")
                .load();

        new DFAsserts(df2, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

}
