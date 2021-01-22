package com.nhl.dflib.avro;

import com.nhl.dflib.*;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AvroTest {

    @TempDir
    static File destination;

    static final DataFrame df = DataFrame.newFrame(
            "int", "Integer", "long", "Long", "double", "Double", "bool", "Bool", "String", "byte_array", "LocalDate")
            .columns(
                    IntSeries.forInts(1, 2, 3),
                    Series.forData(11, 12, null),
                    LongSeries.forLongs(Long.MAX_VALUE - 1L, Long.MIN_VALUE + 1L, 5L),
                    Series.forData(21L, 22L, null),
                    DoubleSeries.forDoubles(20.12, 20.123, 20.1235),
                    Series.forData(30.1, 31.45, null),
                    BooleanSeries.forBooleans(true, false, true),
                    Series.forData(Boolean.TRUE, Boolean.FALSE, null),
                    Series.forData("s1", "s2", null),
                    Series.forData(new byte[]{1, 2, 3}, new byte[0], null),
                    Series.forData(LocalDate.of(2020, 1, 5), LocalDate.of(2019, 6, 8), null)
            );

    static final DataFrame empty = DataFrame.newFrame(df.getColumnsIndex()).empty();

    @Test
    public void testSaveData_File_Empty() {
        File file = new File(destination, "testSaveData_File_Empty.avro");
        Avro.saveData(empty, file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        DataFrame loaded = Avro.load(file);
        assertNotNull(loaded);
        new DataFrameAsserts(loaded, empty.getColumnsIndex()).expectHeight(0);
    }

    @Test
    public void testSaveData_File() {
        File file = new File(destination, "testSaveData_File.avro");
        Avro.saveData(df, file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        DataFrame loaded = Avro.load(file);
        assertNotNull(loaded);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(df.height())

                .expectIntColumns(0)
                .expectLongColumns(2)
                .expectDoubleColumns(4)
                .expectBooleanColumns(6)

                .expectRow(0, 1, 11, Long.MAX_VALUE - 1L, 21L, 20.12, 30.1, true, true, "s1", new byte[]{1, 2, 3}, LocalDate.of(2020, 1, 5))
                .expectRow(1, 2, 12, Long.MIN_VALUE + 1L, 22L, 20.123, 31.45, false, false, "s2", new byte[0], LocalDate.of(2019, 6, 8))
                .expectRow(2, 3, null, 5L, null, 20.1235, null, true, null, null, null, null);
    }

    @Test
    public void testSaveSchema_DataFrame() throws UnsupportedEncodingException {

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
}
