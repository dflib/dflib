package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadComplexTypesTest {

    @TempDir
    static Path outBase;

    // https://avro.apache.org/docs/1.12.0/specification/#complex-types

    @Test
    public void arrayOfStrings() {
        record R(List<String> list, List<String> nullableList) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"list", "type":{"type":"array", "items":"string"}},
                            {"name":"nullableList", "type":["null", {"type":"array", "items":"string"}]}
                          ]
                        }""")
                .writer((s, r, o) -> {
                    r.put("list", new GenericData.Array<>(s.getField("list").schema(), o.list()));

                    if (o.nullableList() != null) {
                        r.put("nullableList", new GenericData.Array<>(s.getField("nullableList").schema().getTypes().get(1), o.nullableList()));
                    }
                })
                .write(
                        new R(List.of("a", "b"), null),
                        new R(List.of("a"), List.of("b")),
                        new R(List.of(), null));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "list", "nullableList")
                .expectHeight(3)
                .expectRow(0, List.of("a", "b"), null)
                .expectRow(1, List.of("a"), List.of("b"))
                .expectRow(2, List.of(), null);
    }

    @Test
    public void arrayOfDoubles() {
        record R(List<Double> list, List<Double> nullableList) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"list", "type":{"type":"array", "items":"double"}},
                            {"name":"nullableList", "type":["null", {"type":"array", "items":"double"}]}
                          ]
                        }""")
                .writer((s, r, o) -> {
                    r.put("list", new GenericData.Array<>(s.getField("list").schema(), o.list()));

                    if (o.nullableList() != null) {
                        r.put("nullableList", new GenericData.Array<>(s.getField("nullableList").schema().getTypes().get(1), o.nullableList()));
                    }
                })
                .write(
                        new R(List.of(1.1, -2.05), null),
                        new R(List.of(5.7), List.of(3.21)),
                        new R(List.of(), null));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "list", "nullableList")
                .expectHeight(3)
                .expectRow(0, List.of(1.1, -2.05), null)
                .expectRow(1, List.of(5.7), List.of(3.21))
                .expectRow(2, List.of(), null);
    }

    @Test
    public void mapOfDoubles() {
        record R(Map<String, Double> map, Map<String, Double> nullableMap) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"map", "type":{"type":"map", "values":"double"}},
                            {"name":"nullableMap", "type":["null", {"type":"map", "values":"double"}]}
                          ]
                        }""")
                .writer((s, r, o) -> {
                    r.put("map", o.map());

                    if (o.nullableMap() != null) {
                        r.put("nullableMap", o.nullableMap());
                    }
                })
                .write(
                        new R(Map.of("a", 1.1, "b", -2.05), null),
                        new R(Map.of("b", 5.7), Map.of("c", 3.21)),
                        new R(Map.of(), null));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "map", "nullableMap")
                .expectHeight(3)
                .expectRow(0, Map.of("a", 1.1, "b", -2.05), null)
                .expectRow(1, Map.of("b", 5.7), Map.of("c", 3.21))
                .expectRow(2, Map.of(), null);
    }

    @Test
    public void mapOfLists() {
        record R(Map<String, List<String>> map, Map<String, List<String>> nullableMap) {
        }

        List<String> listWithNulls = new ArrayList<>();
        listWithNulls.add("le");
        listWithNulls.add(null);

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"map", "type":{"type":"map", "values":{"type":"array", "items":"string"}}},
                            {"name":"nullableMap", "type":["null", {"type":"map", "values":{"type":"array", "items":["null", "string"]}}]}
                          ]
                        }""")
                .writer((s, r, o) -> {

                    Schema listElementSchema = s.getField("map").schema().getValueType();
                    Map<String, GenericData.Array<?>> writableMap = new HashMap<>();
                    o.map().forEach((k, v) -> writableMap.put(k, new GenericData.Array<>(listElementSchema, v)));

                    r.put("map", writableMap);

                    if (o.nullableMap() != null) {
                        Schema nullableListElementSchema = s.getField("nullableMap").schema().getTypes().get(1).getValueType();

                        Map<String, GenericData.Array<?>> writableNullableMap = new HashMap<>();
                        o.nullableMap().forEach((k, v) -> writableNullableMap.put(k, new GenericData.Array<>(nullableListElementSchema, v)));

                        r.put("nullableMap", writableNullableMap);
                    }
                })
                .write(
                        new R(Map.of("a", List.of("la", "lb"), "b", List.of("lc")), null),
                        new R(Map.of("b", List.of("ld")), Map.of("c", listWithNulls)),
                        new R(Map.of(), null));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "map", "nullableMap")
                .expectHeight(3)
                .expectRow(0, Map.of("a", List.of("la", "lb"), "b", List.of("lc")), null)
                .expectRow(1, Map.of("b", List.of("ld")), Map.of("c", listWithNulls))
                .expectRow(2, Map.of(), null);
    }

    @Test
    public void records() {
        record RS(String a, int b) {
        }
        record R(RS r, RS nullableR) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"r", "type": {"type":"record", "name":"rs", "fields":[{"name":"a","type":"string"}, {"name":"b","type":"int"} ]}},
                            {"name":"nullableR", "type": ["null", "rs"]}
                          ]
                        }""")
                .writer((s, r, o) -> {
                    GenericData.Record or = new GenericData.Record(s.getField("r").schema());
                    or.put("a", o.r().a());
                    or.put("b", o.r().b());
                    r.put("r", or);

                    if (o.nullableR() != null) {
                        GenericData.Record onr = new GenericData.Record(s.getField("nullableR").schema().getTypes().get(1));
                        onr.put("a", o.nullableR().a());
                        onr.put("b", o.nullableR().b());

                        r.put("nullableR", onr);
                    }
                })
                .write(
                        new R(new RS("a", 5), null),
                        new R(new RS("b", 7), new RS("c", 8)));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "r", "nullableR")
                .expectHeight(2)
                .expectRow(0, Map.of("a", "a", "b", 5), null)
                .expectRow(1, Map.of("a", "b", "b", 7), Map.of("a", "c", "b", 8));
    }

    @Test
    public void enumType() {
        record R(String e, String eNullable) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"e", "type":{"type":"enum", "name":"e1", "symbols" : ["A", "B", "C"]}},
                            {"name":"eNullable", "type":["null", {"type":"enum", "name":"e1", "symbols" : ["A", "B", "C"]}]}
                          ]
                        }""")
                .writer((s, r, o) -> {
                    r.put("e", new GenericData.EnumSymbol(s.getField("e").schema(), o.e()));

                    if (o.eNullable() != null) {
                        r.put("eNullable", new GenericData.EnumSymbol(s.getField("eNullable").schema().getTypes().get(1), o.eNullable()));
                    }
                })
                .write(
                        new R("A", null),
                        new R("C", "A"));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df,
                "e", "eNullable")
                .expectHeight(2)
                .expectRow(0, "A", null)
                .expectRow(1, "C", "A");
    }

    @Test
    public void fixed() {
        record R(byte[] bytes, byte[] bytesNullable) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"bytes", "type":{"type":"fixed", "size":4, "name":"byte4"}},
                            {"name":"bytesNullable", "type":[{"type":"fixed", "size":4, "name":"byte4"}, "null"]}
                          ]
                        }""")
                .writer((s, r, o) -> {
                    r.put("bytes", new GenericData.Fixed(s.getField("bytes").schema(), o.bytes()));

                    if (o.bytesNullable() != null) {
                        r.put("bytesNullable", new GenericData.Fixed(s.getField("bytesNullable").schema().getTypes().get(0), o.bytesNullable()));
                    }

                })
                .write(
                        new R(new byte[]{1, 2, 3, 4}, null),
                        new R(new byte[]{5, 6, 7, 8}, new byte[]{9, 10, 11, 12}));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "bytes", "bytesNullable")
                .expectHeight(2)
                .expectRow(0, new byte[]{1, 2, 3, 4}, null)
                .expectRow(1, new byte[]{5, 6, 7, 8}, new byte[]{9, 10, 11, 12});
    }
}
