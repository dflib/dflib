package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.jdbc.unit.dbadapter.TestDbAdapter;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;

public class TableLoader_SamplingIT extends BaseDbTest {

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void test(TestDbAdapter adapter) {
        adapter.delete("t1");
        JdbcConnector connector = adapter.createConnector();

        adapter.getTable("t1")
                .insertColumns("id", "name")
                .values(2L, "n2")
                .values(3L, "n3")
                .values(4L, "n4")
                .values(5L, "n5")
                .values(6L, "n6")
                .values(7L, "n7")
                .exec();

        // using fixed Random seed to get reproducible result

        DataFrame df = connector
                .tableLoader("t1")
                .includeColumns("id", "name")
                .sampleRows(2, new Random(8))
                .load();

        new DataFrameAsserts(df, "id", "name")
                .expectHeight(2)
                .expectRow(0, 6L, "n6")
                .expectRow(1, 7L, "n7");

        // do another test with different random seed
        DataFrame df2 = connector
                .tableLoader("t1")
                .includeColumns("id", "name")
                .sampleRows(2, new Random(15))
                .load();

        new DataFrameAsserts(df2, "id", "name")
                .expectHeight(2)
                .expectRow(0, 3L, "n3")
                .expectRow(1, 4L, "n4");

        // and one more test
        DataFrame df3 = connector
                .tableLoader("t1")
                .includeColumns("id", "name")
                .sampleRows(3, new Random(3))
                .load();

        new DataFrameAsserts(df3, "id", "name")
                .expectHeight(3)
                .expectRow(0, 3L, "n3")
                .expectRow(1, 5L, "n5")
                .expectRow(2, 7L, "n7");
    }

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSampleRows_SampleLargerThanResultSet(TestDbAdapter adapter) {
        adapter.delete("t1");
        JdbcConnector connector = adapter.createConnector();

        adapter.getTable("t1")
                .insertColumns("id", "name")
                .values(1L, "n1")
                .values(2L, "n2")
                .exec();

        // using fixed Random seed to get reproducible result
        DataFrame df = connector
                .tableLoader("t1")
                .includeColumns("id", "name")
                .sampleRows(5, new Random(8))
                .load();

        new DataFrameAsserts(df, "id", "name")
                .expectHeight(2)
                .expectRow(0, 1L, "n1")
                .expectRow(1, 2L, "n2");
    }

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testSampleRows_SampleSameAsCSV(TestDbAdapter adapter) {
        adapter.delete("t1");
        JdbcConnector connector = adapter.createConnector();

        adapter.getTable("t1")
                .insertColumns("id", "name")
                .values(1L, "n1")
                .values(2L, "n2")
                .exec();

        // using fixed Random seed to get reproducible result
        DataFrame df = connector
                .tableLoader("t1")
                .includeColumns("id", "name")
                .sampleRows(2, new Random(8))
                .load();

        new DataFrameAsserts(df, "id", "name")
                .expectHeight(2)
                .expectRow(0, 1L, "n1")
                .expectRow(1, 2L, "n2");
    }
}
