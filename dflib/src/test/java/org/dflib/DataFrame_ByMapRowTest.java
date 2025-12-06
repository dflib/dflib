package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFrame_ByMapRowTest {

    @Test
    public void allColumns() {
        List<Map<String, ?>> data = List.of(
                Map.of("a", 1, "b", "x"),
                Map.of("a", 2, "b", "y")
        );

        DataFrame df = DataFrame
                .byMapRow()
                .of(data);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void allColumns_DifferentKeys() {
        List<Map<String, ?>> data = List.of(
                Map.of("a", 1, "b", "x"),
                Map.of("a", 2, "c", "z"),
                Map.of("b", "y", "c", "w")
        );

        DataFrame df = DataFrame
                .byMapRow()
                .of(data);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", null)
                .expectRow(1, 2, null, "z")
                .expectRow(2, null, "y", "w");
    }

    @Test
    public void allColumns_EmptyMap() {
        List<Map<String, ?>> data = List.of(
                Map.of("a", 1, "b", "x"),
                Map.of(),
                Map.of("a", 3, "b", "z")
        );

        DataFrame df = DataFrame
                .byMapRow()
                .of(data);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, null, null)
                .expectRow(2, 3, "z");
    }

    @Test
    public void allColumns_SingleMap() {
        List<Map<String, ?>> data = List.of(
                Map.of("a", 1, "b", "x", "c", 3.14)
        );

        DataFrame df = DataFrame
                .byMapRow()
                .of(data);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(1)
                .expectRow(0, 1, "x", 3.14);
    }

    @Test
    public void allColumns_EmptyIterable() {
        List<Map<String, ?>> data = List.of();

        DataFrame df = DataFrame
                .byMapRow()
                .of(data);

        new DataFrameAsserts(df).expectHeight(0);
    }

    @Test
    public void allColumns_NullValues() {
        List<Map<String, ?>> data = List.of(
                Map.of("a", 1, "b", "x"),
                Map.of("a", 2),
                new HashMap() {{
                    put("a", null);
                    put("b", "z");
                }}
        );

        DataFrame df = DataFrame
                .byMapRow()
                .of(data);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, null)
                .expectRow(2, null, "z");
    }

    @Test
    public void selectiveColumns_ByName() {
        List<Map<String, ?>> data = List.of(
                Map.of("a", 1, "b", "x", "c", 100),
                Map.of("a", 2, "b", "y", "c", 200)
        );

        DataFrame df = DataFrame
                .byMapRow()
                .columnNames("a", "b")
                .of(data);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void selectiveColumns_ByName_MissingKeys() {
        // Some maps don't have all requested columns
        List<Map<String, ?>> data = List.of(
                Map.of("a", 1, "b", "x"),
                Map.of("a", 2),          // missing "b"
                Map.of("b", "z")         // missing "a"
        );

        DataFrame df = DataFrame
                .byMapRow()
                .columnNames("a", "b")
                .of(data);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, null)
                .expectRow(2, null, "z");
    }

    @Test
    public void selectiveColumns_ByIndex() {
        List<Map<String, ?>> data = List.of(
                Map.of("a", 1, "b", "x", "c", 100),
                Map.of("a", 2, "b", "y", "c", 200)
        );

        DataFrame df = DataFrame
                .byMapRow()
                .columnIndex(Index.of("a", "c"))
                .of(data);

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100)
                .expectRow(1, 2, 200);
    }

    @Test
    public void selectiveColumns_EmptyIterable() {
        List<Map<String, ?>> data = List.of();

        DataFrame df = DataFrame
                .byMapRow()
                .columnNames("a", "b")
                .of(data);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void mixedTypes() {
        // Maps with mixed value types
        List<Map<String, ?>> data = List.of(
                Map.of("str", "hello", "int", 42, "dbl", 3.14),
                Map.of("str", "world", "int", 99, "dbl", 2.71)
        );

        DataFrame df = DataFrame
                .byMapRow()
                .of(data);

        new DataFrameAsserts(df, "dbl", "int", "str")
                .expectHeight(2)
                .expectRow(0, 3.14, 42, "hello")
                .expectRow(1, 2.71, 99, "world");
    }

    @Test
    public void columnOrder_AllColumns() {
        List<Map<String, ?>> data = List.of(
                Map.of("z", 1, "a", 2, "m", 3, "h", 4)
        );

        DataFrame df = DataFrame
                .byMapRow()
                .of(data);

        new DataFrameAsserts(df, "a", "h", "m", "z").expectHeight(1);
    }

    @Test
    public void columnOrder_SelectiveColumns() {
        List<Map<String, ?>> data = List.of(
                Map.of("z", 1, "a", 2, "m", 3, "h", 4)
        );

        DataFrame df = DataFrame
                .byMapRow()
                .columnNames("z", "m", "h", "a")
                .of(data);

        new DataFrameAsserts(df, "z", "m", "h", "a").expectHeight(1);
    }
}
