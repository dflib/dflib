package com.nhl.dflib.jdbc;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JdbcTableLoaderIT extends BaseDbTest {

    @Test
    public void testLoadTable() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = Jdbc.connector(getDataSource()).table("t1").load();

        new DFAsserts(df, "id", "name", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testLoadTable_IncludeColumns() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = Jdbc.connector(getDataSource())
                .table("t1")
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
        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        T2.insert(
                l1,
                67,
                7.8,
                true,
                "s1",
                ldt,
                ld,
                bytes).insert(
                l1 + 1,
                68,
                10.6,
                false,
                "s2",
                null,
                null,
                null);

        DataFrame df = Jdbc.connector(getDataSource()).table("t2").load();

        new DFAsserts(df, "bigint", "int", "double", "boolean", "string", "timestamp", "date", "bytes")
                .expectHeight(2)
                .expectRow(0, l1, 67, 7.8, true, "s1", Timestamp.valueOf(ldt), Date.valueOf(ld), bytes)
                .expectRow(1, l1 + 1, 68, 10.6, false, "s2", null, null, null);
    }


}
