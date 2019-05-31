package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.Index;
import com.nhl.dflib.unit.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JdbcConnector_TableLoaderIT extends BaseDbTest {

    private JdbcConnector createConnector() {
        return Jdbc.connector(getDataSource());
    }

    @Test
    public void test() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = createConnector()
                .tableLoader("t1")
                .load();

        new DFAsserts(df, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testIncludeColumns() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = createConnector()
                .tableLoader("t1")
                .includeColumns("id", "salary")
                .load();

        new DFAsserts(df, "id", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, 50_000.01)
                .expectRow(1, 2L, 120_000.);
    }

    @Test
    public void testDataTypeConversions() {

        LocalDate ld = LocalDate.of(1977, 02, 05);
        LocalDateTime ldt = LocalDateTime.of(2019, 02, 03, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        T2.insert(l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .insert(null, null, null, false, null, null, null, null, null);

        DataFrame df = createConnector()
                .tableLoader("t2")
                .load();

        new DFAsserts(df, columnNames(T2))
                .expectHeight(2)
                .expectRow(0, l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .expectRow(1, null, null, null, false, null, null, null, null, null);
    }

    @Test
    public void testMaxRows() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 20_000.);

        DataFrame df = createConnector()
                .tableLoader("t1")
                .maxRows(2)
                .load();

        new DFAsserts(df, columnNames(T1))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void testEq_SingleColumn() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 11_000.);

        DataFrame matcher = DataFrame.forSequenceFoldByRow(Index.forLabels("id"), 1L, 3L);

        DataFrame df = createConnector()
                .tableLoader("t1")
                .eq(matcher)
                .includeColumns("name", "salary")
                .load();

        new DFAsserts(df, "name", "salary")
                .expectHeight(2)
                .expectRow(0, "n1", 50_000.01)
                .expectRow(1, "n3", 11_000.);
    }

    @Test
    public void testEq_MultiColumn() {

        T1.insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 11_000.);

        DataFrame matcher = DataFrame.forSequenceFoldByRow(Index.forLabels("id", "name"),
                1L, "n5",
                3L, "n3");

        DataFrame df = createConnector()
                .tableLoader("t1")
                .eq(matcher)
                .includeColumns("name", "salary")
                .load();

        new DFAsserts(df, "name", "salary")
                .expectHeight(1)
                .expectRow(0, "n3", 11_000.);
    }
}
