package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.jdbc.unit.BaseDbTest;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void valueCardinality() {

        adapter.getTable("t1")
                .insert(1L, "ab", 1)
                .insert(2L, "ab", 40_000)
                .insert(3L, "bc", 40_000)
                .insert(4L, "bc", 30_000)
                .insert(5L, null, 30_000)
                .insert(6L, "bc", null);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .cols("salary", "name")
                .load();

        new DataFrameAsserts(df, "salary", "name")
                .expectHeight(6)
                .expectRow(0, 1.0, "ab")
                .expectRow(1, 40000.0, "ab")
                .expectRow(2, 40000.0, "bc")
                .expectRow(3, 30000.0, "bc")
                .expectRow(4, 30000.0, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col(0).mapVal(System::identityHashCode),
                $col(1).mapVal(System::identityHashCode));

        assertEquals(6, idCardinality.getColumn(0).unique().size());
        assertEquals(6, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void valueCardinality_compactCol_Name() {

        adapter.getTable("t1")
                .insert(1L, "ab", 1)
                .insert(2L, "ab", 40_000)
                .insert(3L, "bc", 40_000)
                .insert(4L, "bc", 30_000)
                .insert(5L, null, 30_000)
                .insert(6L, "bc", null);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .cols("salary", "name")
                .compactCol("salary")
                .compactCol("name")
                .load();

        new DataFrameAsserts(df, "salary", "name")
                .expectHeight(6)
                .expectRow(0, 1.0, "ab")
                .expectRow(1, 40000.0, "ab")
                .expectRow(2, 40000.0, "bc")
                .expectRow(3, 30000.0, "bc")
                .expectRow(4, 30000.0, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col(0).mapVal(System::identityHashCode),
                $col(1).mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void includeColumns() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .cols("id", "salary")
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
    public void limit() {

        adapter.getTable("t1")
                .insert(1L, "n1", 50_000.01)
                .insert(2L, "n2", 120_000.)
                .insert(3L, "n3", 20_000.);

        DataFrame df = adapter.createConnector()
                .tableLoader("t1")
                .limit(2)
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
                .cols("name", "salary")
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
                .cols("name", "salary")
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
                .cols("name", "salary")
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
                .cols("name", "salary")
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
                .cols("name", "salary")
                .load();

        new DataFrameAsserts(df, "name", "salary")
                .expectHeight(2)
                .expectRow(0, "n1", 50_000.01)
                .expectRow(1, "n2", 120_000.);
    }
}
