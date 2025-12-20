package org.dflib.json;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;


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

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, Map.of("x", 1, "y", 2))
                .expectRow(1, 2, Map.of("x", 3, "y", 4));
    }

    @Test
    @DisplayName("$.* : root is list, objects are rows, nested lists")
    public void listOfNestedLists() {
        String json = "[{\"a\":1, \"b\":[1, 2]},{\"a\":2, \"b\":[3, 4]}]";
        DataFrame df = Json.loader().load(json);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, List.of(1, 2))
                .expectRow(1, 2, List.of(3, 4));
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
    @DisplayName("$.* : custom mapper")
    public void column_Mapper() {
        String json = "[{\"a\":\"A\"},{\"a\":null},{\"a\":\"B\"},{}]";
        DataFrame df = Json.loader()
                .col("a", c -> c != null ? c : "XXX")
                .load(json);
        new DataFrameAsserts(df, "a")
                .expectHeight(4)
                .expectRow(0, "A")
                .expectRow(1, "XXX")
                .expectRow(2, "B")
                .expectRow(3, "XXX");
    }

    @Test
    @DisplayName("$.* : bool columns")
    public void boolColumn() {
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
    @DisplayName("$.* : int column")
    public void intColumn() {
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
    @DisplayName("$.* : long column")
    public void longColumn() {
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
    @DisplayName("$.* : date column")
    public void dateColumn() {
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
    @DisplayName("$.* : time column")
    public void timeColumn() {
        String json = "[{\"a\":\"00:01:02\"},{\"a\":\"00:02:03\"},{\"a\":\"00:03:04\"}]";
        DataFrame df = Json.loader()
                .timeCol("a")
                .load(json);
        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalTime.parse("00:01:02"))
                .expectRow(1, LocalTime.parse("00:02:03"))
                .expectRow(2, LocalTime.parse("00:03:04"));
    }

    @Test
    @DisplayName("$.* : time column")
    public void timeColumn_Formatter() {
        String json = "[{\"a\":\"00_01_02\"},{\"a\":\"00_02_03\"},{\"a\":\"00_03_04\"}]";
        DataFrame df = Json.loader()
                .timeCol("a", DateTimeFormatter.ofPattern("H_m_s"))
                .load(json);
        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalTime.parse("00:01:02"))
                .expectRow(1, LocalTime.parse("00:02:03"))
                .expectRow(2, LocalTime.parse("00:03:04"));
    }


    @Test
    @DisplayName("$.* : datetime column")
    public void dateTimeColumn() {
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

    @Test
    public void valueCardinality() {
        DataFrame df = new JsonLoader()
                .load("[{\"a\": 1, \"b\": \"ab\"}," +
                        "{\"a\": 40000, \"b\": \"ab\"}," +
                        "{\"a\": 40000, \"b\": \"bc\"}," +
                        "{\"a\": 30000, \"b\": \"bc\"}," +
                        "{\"a\": 30000}," +
                        "{\"b\": \"bc\"}]");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        assertEquals(6, idCardinality.getColumn(0).unique().size());
        assertEquals(6, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void valueCardinality_CompactCol() {
        DataFrame df = new JsonLoader()
                .compactCol("a")
                .compactCol("b")
                .load("[{\"a\": 1, \"b\": \"ab\"}," +
                        "{\"a\": 40000, \"b\": \"ab\"}," +
                        "{\"a\": 40000, \"b\": \"bc\"}," +
                        "{\"a\": 30000, \"b\": \"bc\"}," +
                        "{\"a\": 30000}," +
                        "{\"b\": \"bc\"}]");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void valueCardinality_CompactCol_Mapper() {
        DataFrame df = new JsonLoader()
                .compactCol("b", s -> s != null ? "_" + s : null)
                .load("[{\"a\": 1, \"b\": \"ab\"}," +
                        "{\"a\": 40000, \"b\": \"ab\"}," +
                        "{\"a\": 40000, \"b\": \"bc\"}," +
                        "{\"a\": 30000, \"b\": \"bc\"}," +
                        "{\"a\": 30000}," +
                        "{\"b\": \"bc\"}]");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "_ab")
                .expectRow(1, 40000, "_ab")
                .expectRow(2, 40000, "_bc")
                .expectRow(3, 30000, "_bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "_bc");

        DataFrame idCardinality = df.cols().select(
                $col("b").mapVal(System::identityHashCode));

        assertEquals(3, idCardinality.getColumn(0).unique().size());
    }
}
