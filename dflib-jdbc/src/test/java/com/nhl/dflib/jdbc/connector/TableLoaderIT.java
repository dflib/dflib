package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TableLoaderIT extends BaseDbTest {

    @Test
    public void test() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .load();

        new DataFrameAsserts(df, adapter.getColumnNames("t1"))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void includeColumns() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .includeColumns("id", "salary")
                .load();

        new DataFrameAsserts(df, "id", "salary")
                .expectHeight(2)
                .expectRow(0, 1L, 50_000.01)
                .expectRow(1, 2L, 120_000.);
    }

    @Test
    public void dataTypeConversions() {

        LocalDate ld = LocalDate.of(1977, 02, 05);
        LocalDateTime ldt = LocalDateTime.of(2019, 02, 03, 1, 2, 5);
        LocalTime lt = LocalTime.of(5, 6, 8);

        byte[] bytes = new byte[]{3, 5, 11};
        long l1 = Integer.MAX_VALUE + 1L;

        adapter.getTable("t2")
                .insert(l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .insert(null, null, null, false, null, null, null, null, null);

        DataFrame df = adapter.createConnector()
                .tableLoader("t2")
                .load();

        new DataFrameAsserts(df, adapter.getColumnNames("t2"))
                .expectHeight(2)
                .expectRow(0, l1, 67, 7.8, true, "s1", ldt, ld, lt, bytes)
                .expectRow(1, null, null, null, false, null, null, null, null, null);
    }

    @Test
    public void maxRows() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 20_000.);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .maxRows(2)
                .load();

        new DataFrameAsserts(df, adapter.getColumnNames("t1"))
                .expectHeight(2)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.);
    }

    @Test
    public void eq_SingleColumn() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 11_000.);

        DataFrame matcher = DataFrame.foldByRow("id").of(1L, 3L);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .eq(matcher)
                .includeColumns("name", "salary")
                .load();

        new DataFrameAsserts(df, "name", "salary")
                .expectHeight(2)
                .expectRow(0, "n1", 50_000.01)
                .expectRow(1, "n3", 11_000.);
    }

    @Test
    public void eq_MultiColumn() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 11_000.);

        DataFrame matcher = DataFrame.foldByRow("id", "name").of(
                1L, "n5",
                3L, "n3");

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .eq(matcher)
                .includeColumns("name", "salary")
                .load();

        new DataFrameAsserts(df, "name", "salary")
                .expectHeight(1)
                .expectRow(0, "n3", 11_000.);
    }

    @Test
    public void eq_EmptyCondition() {

        adapter.getTable("t1").insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 11_000.);

        DataFrame empty = DataFrame.empty("id", "name");

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .eq(empty)
                .load();

        new DataFrameAsserts(df, "id", "name", "salary").expectHeight(0);
    }

    @Test
    public void eq_EmptyCondition_CustomColumns() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 11_000.);

        DataFrame empty = DataFrame.empty("id", "name");

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .includeColumns("name", "salary")
                .eq(empty)
                .load();

        new DataFrameAsserts(df, "name", "salary").expectHeight(0);
    }

    @Test
    public void neq_SingleColumn() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 11_000.);

        DataFrame matcher = DataFrame.foldByRow("id").of(1L, 3L);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .neq(matcher)
                .includeColumns("name", "salary")
                .load();

        new DataFrameAsserts(df, "name", "salary")
                .expectHeight(1)
                .expectRow(0, "n2", 120_000.);
    }

    @Test
    public void neq_MultiColumn() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 11_000.);

        DataFrame matcher = DataFrame.foldByRow("id", "name").of(
                1L, "n5",
                3L, "n3");

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .neq(matcher)
                .includeColumns("name", "salary")
                .load();

        new DataFrameAsserts(df, "name", "salary")
                .expectHeight(2)
                .expectRow(0, "n1", 50_000.01)
                .expectRow(1, "n2", 120_000.);
    }
}
