package com.nhl.dflib.avro;

import com.nhl.dflib.*;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Avro_SchemaSerializationTest {

    @Test
    public void testLoadSchema() {

        String schemaJson = "{\"type\":\"record\",\"name\":\"x\",\"namespace\":\"com.foo\",\"fields\":[" +
                "{\"name\":\"c\",\"type\":\"bytes\"}]}";

        ByteArrayInputStream in = new ByteArrayInputStream(schemaJson.getBytes(StandardCharsets.UTF_8));
        Schema loaded = Avro.loadSchema(in);

        Schema ref = SchemaBuilder
                .record("x")
                .namespace("com.foo")
                .fields().name("c").type(Schema.create(Schema.Type.BYTES)).noDefault()
                .endRecord();

        assertEquals(ref, loaded);
    }

    @Test
    public void testSaveSchema_DataFrame() throws UnsupportedEncodingException {

        DataFrame df = DataFrame.newFrame(
                "int", "Integer", "long", "Long", "double", "Double", "bool", "Bool", "String", "byte_array", "LocalDate")
                .columns(
                        IntSeries.forInts(1, 2),
                        Series.forData(11, null),
                        LongSeries.forLongs(Long.MAX_VALUE - 1L, 5L),
                        Series.forData(21L, null),
                        DoubleSeries.forDoubles(20.12, 20.1235),
                        Series.forData(30.1, null),
                        BooleanSeries.forBooleans(true, true),
                        Series.forData(Boolean.TRUE, null),
                        Series.forData("s1", null),
                        Series.forData(new byte[]{1, 2, 3}, null),
                        Series.forData(LocalDate.of(2020, 1, 5), null)
                );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Avro.saveSchema(df, out);

        assertEquals("{\"type\":\"record\",\"name\":\"DataFrame\",\"namespace\":\"com.nhl.dflib\",\"fields\":[" +
                        "{\"name\":\"int\",\"type\":\"int\"}," +
                        "{\"name\":\"Integer\",\"type\":[\"int\",\"null\"]}," +
                        "{\"name\":\"long\",\"type\":\"long\"}," +
                        "{\"name\":\"Long\",\"type\":[\"long\",\"null\"]}," +
                        "{\"name\":\"double\",\"type\":\"double\"}," +
                        "{\"name\":\"Double\",\"type\":[\"double\",\"null\"]}," +
                        "{\"name\":\"bool\",\"type\":\"boolean\"}," +
                        "{\"name\":\"Bool\",\"type\":[\"boolean\",\"null\"]}," +
                        "{\"name\":\"String\",\"type\":[{\"type\":\"string\",\"logicalType\":\"dflib-string\"},\"null\"]}," +
                        "{\"name\":\"byte_array\",\"type\":[{\"type\":\"bytes\",\"logicalType\":\"dflib-byte-array\"},\"null\"]}," +
                        "{\"name\":\"LocalDate\",\"type\":[{\"type\":\"int\",\"logicalType\":\"dflib-local-date\"},\"null\"]}]}",
                out.toString(StandardCharsets.UTF_8.name()));
    }

    @Test
    public void testSaveSchema_ExplicitSchema() throws UnsupportedEncodingException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Schema schema = SchemaBuilder
                .record("x")
                .namespace("com.foo")
                .fields().name("c").type(Schema.create(Schema.Type.BYTES)).noDefault()
                .endRecord();

        Avro.saveSchema(schema, out);

        assertEquals("{\"type\":\"record\",\"name\":\"x\",\"namespace\":\"com.foo\",\"fields\":[" +
                "{\"name\":\"c\",\"type\":\"bytes\"}]}", out.toString(StandardCharsets.UTF_8.name()));
    }
}
