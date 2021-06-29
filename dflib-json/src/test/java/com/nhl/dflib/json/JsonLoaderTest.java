package com.nhl.dflib.json;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class JsonLoaderTest {

    @Test
    @DisplayName("$.*.a : single column with default label")
    public void testSingleColumn() {
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
    public void testObjectProperties() {
        String json = "{\"a\": {\"x\":1, \"y\":2}, \"b\":{\"x\":3, \"y\":4}}";
        DataFrame df = Json.loader().load(json);
        new DataFrameAsserts(df, "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    @DisplayName("$ : root is object, root is single row")
    public void testObject_RootPath() {
        String json = "{\"a\":1, \"b\":\"B\"}";
        DataFrame df = Json.loader().pathExpression("$").load(json);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, "B");
    }

    @Test
    @DisplayName("$.* : root is list, objects are rows")
    public void testListOfObjects() {
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
    public void testListOfObjects_Sparse() {
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
    public void testListOfNestedObjects() {
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
    public void testListOfObjects_AttributeSelector_NullsForMissingLeafs() {
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
    public void testListNullsForMissingLeafs() {
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
    public void testListSkupNulls() {
        String json = "[{\"a\":1},{\"b\":2},{\"a\":3}]";
        DataFrame df = Json.loader().pathExpression("$.*.a").load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 3);
    }
}
