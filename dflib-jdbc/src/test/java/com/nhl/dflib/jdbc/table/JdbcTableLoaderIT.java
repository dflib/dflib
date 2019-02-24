package com.nhl.dflib.jdbc.table;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JdbcTableLoaderIT extends BaseDbTest {

    @Test
    public void testLoadTable() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = Jdbc.connector(getDataSource())
                .tableLoader("t1")
                .load();

        new DFAsserts(df, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testLoadTable_IncludeColumns() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = Jdbc.connector(getDataSource())
                .tableLoader("t1")
                .includeColumns("id", "salary")
                .load();

        new DFAsserts(df, "id", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, 50_000.01)
                .expectRow(1, 2L, 120_000.);
    }

    @Test
    public void testLoadDataType_Default() {

        LocalDate ld = LocalDate.of(1977, 02, 05);
        LocalDateTime ldt = LocalDateTime.of(2019, 02, 03, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        T2.insert(l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .insert(null, null, null, false, null, null, null, null, null);

        DataFrame df = Jdbc.connector(getDataSource())
                .tableLoader("t2")
                .load();

        new DFAsserts(df, columnNames(T2))
                .expectHeight(2)
                .expectRow(0, l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .expectRow(1, null, null, null, false, null, null, null, null, null);
    }

    @Test
    public void testLoadTable_MaxRows() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 20_000.);

        DataFrame df = Jdbc.connector(getDataSource())
                .tableLoader("t1")
                .maxRows(2)
                .load();

        new DFAsserts(df, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }
}
