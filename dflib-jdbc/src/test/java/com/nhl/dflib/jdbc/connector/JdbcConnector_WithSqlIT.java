package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.Test;

public class JdbcConnector_WithSqlIT extends BaseDbTest {

    private JdbcConnector createConnector() {
        return Jdbc.connector(getDataSource());
    }

    @Test
    public void test() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 1_000.);

        DataFrame df = createConnector()
                .withSql("SELECT \"id\", \"salary\" from \"t1\" WHERE \"id\" > 1")
                .load();

        new DFAsserts(df, "id", "salary")
                .expectHeight(2)
                .expectRow(0, 2L, 120_000.)
                .expectRow(1, 3L, 1_000.);
    }

    @Test
    public void testMaxRows() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 20_000.);

        DataFrame df = createConnector()
                .withSql("SELECT * from \"t1\"")
                .maxRows(2)
                .load();

        new DFAsserts(df, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }
}
