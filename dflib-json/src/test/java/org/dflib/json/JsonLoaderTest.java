package org.dflib.json;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class JsonLoaderTest {

    @Test
    @DisplayName("$.*.a : single column with default label")
    public void singleColumn() {
        String json = "[{\"a\":1},{\"a\":2},{\"a\":3}]";
        DataFrame df = Json.loader().pathExpression("$.*.a").load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }

    @Test
    @DisplayName("$.* : root is object, properties are rows")
    public void objectProperties() {
        String json = "{\"a\": {\"x\":1, \"y\":2}, \"b\":{\"x\":3, \"y\":4}}";
        DataFrame df = Json.loader().load(json);
        new DataFrameAsserts(df, "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    @DisplayName("$ : root is object, root is single row")
    public void object_RootPath() {
        String json = "{\"a\":1, \"b\":\"B\"}";
        DataFrame df = Json.loader().pathExpression("$").load(json);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, "B");
    }

    @Test
    @DisplayName("$.* : root is list, objects are rows")
    public void listOfObjects() {
        String json = "[{\"a\":1, \"b\":\"B\"},{\"a\":2, \"b\":\"C\"},{\"a\":3, \"b\":\"D\"}]";
        DataFrame df = Json.loader().load(json);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "B")
                .expectRow(1, 2, "C")
                .expectRow(2, 3, "D");
    }

    @Test
    @DisplayName("$.* : root is list, objects are rows, different property names")
    public void listOfObjects_Sparse() {
        String json = "[{\"a\":1, \"b\":\"B\"},{\"b\":\"C\"},{\"a\":3, \"c\":\"D\"}]";

        DataFrame df = Json.loader().load(json);
        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "B", null)
                .expectRow(1, null, "C", null)
                .expectRow(2, 3, null, "D");
    }

    @Test
    @DisplayName("$.* : root is list, objects are rows, nested maps")
    public void listOfNestedObjects() {
        String json = "[{\"a\":1, \"b\":{\"x\":1, \"y\":2}},{\"a\":2, \"b\":{\"x\":3, \"y\":4}}]";
        DataFrame df = Json.loader().load(json);

        Map<String, Object> expectedMap1 = new HashMap<>();
        expectedMap1.put("x", 1);
        expectedMap1.put("y", 2);

        Map<String, Object> expectedMap2 = new HashMap<>();
        expectedMap2.put("x", 3);
        expectedMap2.put("y", 4);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, expectedMap1)
                .expectRow(1, 2, expectedMap2);
    }

    @Test
    @DisplayName("$..['a', 'c'] : selecting specific properties, nulls must be included if no match")
    public void listOfObjects_AttributeSelector_NullsForMissingLeafs() {
        String json = "[{\"a\":1, \"b\":\"B\"},{\"b\":\"C\"},{\"a\":3, \"c\":\"D\"}]";

        DataFrame df = Json.loader().pathExpression("$..['a', 'c']").nullsForMissingLeafs().load(json);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, null)
                .expectRow(1, null, null)
                .expectRow(2, 3, "D");
    }

    @Test
    @DisplayName("$.*.a : nulls must be included if no match")
    public void listNullsForMissingLeafs() {
        String json = "[{\"a\":1},{\"b\":2},{\"a\":3}]";
        DataFrame df = Json.loader().pathExpression("$.*.a").nullsForMissingLeafs().load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, null)
                .expectRow(2, 3);
    }

    @Test
    @DisplayName("$.*.a : nulls must be excluded")
    public void listSkipNulls() {
        String json = "[{\"a\":1},{\"b\":2},{\"a\":3}]";
        DataFrame df = Json.loader().pathExpression("$.*.a").load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 3);
    }

    @Test
    @DisplayName("$.* : bool preset columns")
    public void test_BoolPresetColumns() {
        String json = "[{\"a\":true},{\"a\":\"true\"},{\"a\":false},{}]";
        DataFrame df = Json.loader()
                .boolCol("a")
                .load(json);
        new DataFrameAsserts(df, "a")
                .expectHeight(4)
                .expectBooleanColumns(0)
                .expectRow(0, true)
                .expectRow(1, true)
                .expectRow(2, false)
                .expectRow(3, false);
    }

    @Test
    @DisplayName("$.* : int preset columns")
    public void test_IntPresetColumns() {
        String json = "[{\"a\":1},{\"a\":\"2\", \"b\":3},{\"a\":4}]";
        DataFrame df = Json.loader()
                .intCol("a")
                .intCol("b", -2)
                .load(json);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, 1, -2)
                .expectRow(1, 2, 3)
                .expectRow(2, 4, -2);
    }

    @Test
    @DisplayName("$.* : long preset columns")
    public void test_LongPresetColumns() {
        String json = "[{\"a\":1},{\"a\":\"2\", \"b\":3},{\"a\":4}]";
        DataFrame df = Json.loader()
                .longColumn("a")
                .longColumn("b", -2L)
                .load(json);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, 1L, -2L)
                .expectRow(1, 2L, 3L)
                .expectRow(2, 4L, -2L);
    }

    @Test
    @DisplayName("$.* : date preset columns")
    public void test_DatePresetColumns() {
        String json = "[{\"a\":\"2021-01-15\"},{\"a\":\"2022-03-16\"},{\"a\":\"2023-03-18\"}]";
        DataFrame df = Json.loader()
                .dateCol("a")
                .load(json);
        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.parse("2021-01-15"))
                .expectRow(1, LocalDate.parse("2022-03-16"))
                .expectRow(2, LocalDate.parse("2023-03-18"));
    }

    @Test
    @DisplayName("$.* : datetime preset columns")
    public void test_DateTimePresetColumns() {
        String json = "[{\"a\":\"2021-01-15T00:01:02\"},{\"a\":\"2022-03-16T00:02:03\"},{\"a\":\"2023-03-18T00:03:04\"}]";
        DataFrame df = Json.loader()
                .dateTimeCol("a")
                .load(json);
        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDateTime.parse("2021-01-15T00:01:02"))
                .expectRow(1, LocalDateTime.parse("2022-03-16T00:02:03"))
                .expectRow(2, LocalDateTime.parse("2023-03-18T00:03:04"));
    }
}
