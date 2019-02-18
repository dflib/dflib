package com.nhl.dflib.jdbc;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.Test;

public class JdbcTableLoaderIT extends BaseDbTest {

    @Test
    public void testLoadTable() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = Jdbc.connector(getDataSource()).tableLoader("t1").load();

        new DFAsserts(df, "id", "name", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }
}
