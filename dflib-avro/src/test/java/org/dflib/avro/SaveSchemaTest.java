package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaveSchemaTest {

    @Test
    public void dataFrameSchema() {

        DataFrame df = DataFrame.byColumn(
                        "int", "Integer", "long", "Long", "double", "Double", "bool", "Bool", "String", "byte_array", "LocalDate")
                .of(
                        Series.ofInt(1, 2),
                        Series.of(11, null),
                        Series.ofLong(Long.MAX_VALUE - 1L, 5L),
                        Series.of(21L, null),
                        Series.ofDouble(20.12, 20.1235),
                        Series.of(30.1, null),
                        Series.ofBool(true, true),
                        Series.of(Boolean.TRUE, null),
                        Series.of("s1", null),
                        Series.of(new byte[]{1, 2, 3}, null),
                        Series.of(LocalDate.of(2020, 1, 5), null)
                );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Avro.saveSchema(df, out);

        assertEquals("""
                        {"type":"record","name":"DataFrame","namespace":"org.dflib","fields":[\
                        {"name":"int","type":"int"},\
                        {"name":"Integer","type":["int","null"]},\
                        {"name":"long","type":"long"},\
                        {"name":"Long","type":["long","null"]},\
                        {"name":"double","type":"double"},\
                        {"name":"Double","type":["double","null"]},\
                        {"name":"bool","type":"boolean"},\
                        {"name":"Bool","type":["boolean","null"]},\
                        {"name":"String","type":["string","null"]},\
                        {"name":"byte_array","type":[{"type":"bytes","logicalType":"dflib-bytearray"},"null"]},\
                        {"name":"LocalDate","type":[{"type":"long","logicalType":"dflib-localdate"},"null"]}]}""",
                out.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void builderSchema() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Schema schema = SchemaBuilder
                .record("x")
                .namespace("com.foo")
                .fields().name("c").type(Schema.create(Schema.Type.BYTES)).noDefault()
                .endRecord();

        Avro.saveSchema(schema, out);

        assertEquals("""
                        {"type":"record","name":"x","namespace":"com.foo","fields":[{"name":"c","type":"bytes"}]}""",
                out.toString(StandardCharsets.UTF_8));
    }
}
