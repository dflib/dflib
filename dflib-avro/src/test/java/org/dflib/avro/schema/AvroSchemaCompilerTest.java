package org.dflib.avro.schema;

import org.apache.avro.Schema;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvroSchemaCompilerTest {

    @Test
    public void compileSchema() {
        DataFrame df = DataFrame.byColumn(
                        "int", "Integer", "long", "Long", "double", "Double", "bool", "Bool", "String", "Nulls")
                .of(
                        Series.ofInt(1, 3),
                        Series.of(11, null),
                        Series.ofLong(4L, 5L),
                        Series.of(21L, null),
                        Series.ofDouble(20.12, 20.1235),
                        Series.of(30.1, null),
                        Series.ofBool(true, false),
                        Series.of(Boolean.TRUE, null),
                        Series.of("s1", null),
                        Series.of(null, null)
                );

        Schema schema = new AvroSchemaCompiler()
                .name("s")
                .namespace("com.foo")
                .compileSchema(df);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"record","name":"s","namespace":"com.foo","fields":[\
                {"name":"int","type":"int"},\
                {"name":"Integer","type":["int","null"]},\
                {"name":"long","type":"long"},\
                {"name":"Long","type":["long","null"]},\
                {"name":"double","type":"double"},\
                {"name":"Double","type":["double","null"]},\
                {"name":"bool","type":"boolean"},\
                {"name":"Bool","type":["boolean","null"]},\
                {"name":"String","type":["string","null"]},\
                {"name":"Nulls","type":"null"}]}""", schema.toString());
    }

    @Test
    public void compileSchema_EmptyDF() {

        DataFrame empty = DataFrame.empty("A", "B");

        Schema schema = new AvroSchemaCompiler()
                .name("s")
                .namespace("com.foo")
                .compileSchema(empty);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"record","name":"s","namespace":"com.foo","fields":[\
                {"name":"A","type":[{"type":"string","logicalType":"dflib-unmapped"},"null"]},\
                {"name":"B","type":[{"type":"string","logicalType":"dflib-unmapped"},"null"]}]}""", schema.toString());
    }

    @Test
    public void compileSchema_DefaultNames() {
        DataFrame empty = DataFrame.empty("A");
        Schema schema = new AvroSchemaCompiler().compileSchema(empty);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"record","name":"DataFrame","namespace":"org.dflib","fields":[\
                {"name":"A","type":[{"type":"string","logicalType":"dflib-unmapped"},"null"]}]}""", schema.toString());
    }

    @Test
    public void enumSchema() {

        Schema schema = new AvroSchemaCompiler().enumSchema(_TestEnum.class);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"enum","name":"_TestEnum","doc":"org.dflib.avro.schema","symbols":["m","_x","V"],\
                "dflib.enum.type":"org.dflib.avro.schema.AvroSchemaCompilerTest$_TestEnum"}""", schema.toString());
    }

    @Test
    public void localTimestampPrecision_Default() {

        DataFrame df = DataFrame.byColumn("t").of(Series.of(LocalDateTime.of(1, 1, 1, 1, 1, 1)));
        Schema schema = new AvroSchemaCompiler().compileSchema(df);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"record","name":"DataFrame","namespace":"org.dflib","fields":[\
                {"name":"t","type":[{"type":"long","logicalType":"local-timestamp-micros"},"null"]}]}""", schema.toString());
    }

    @Test
    public void timePrecision_Default() {

        DataFrame df = DataFrame.byColumn("t").of(Series.of(LocalTime.of(1, 1, 1)));
        Schema schema = new AvroSchemaCompiler().compileSchema(df);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"record","name":"DataFrame","namespace":"org.dflib","fields":[\
                {"name":"t","type":[{"type":"long","logicalType":"time-micros"},"null"]}]}""", schema.toString());
    }

    @Test
    public void timePrecision_Millis() {

        DataFrame df = DataFrame.byColumn("t").of(Series.of(LocalTime.of(1, 1, 1)));
        Schema schema = new AvroSchemaCompiler().timeUnit(SchemaTimeUnit.MILLIS).compileSchema(df);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"record","name":"DataFrame","namespace":"org.dflib","fields":[\
                {"name":"t","type":[{"type":"int","logicalType":"time-millis"},"null"]}]}""", schema.toString());
    }

    @Test
    public void timePrecision_Micros() {

        DataFrame df = DataFrame.byColumn("t").of(Series.of(LocalTime.of(1, 1, 1)));
        Schema schema = new AvroSchemaCompiler().timeUnit(SchemaTimeUnit.MICROS).compileSchema(df);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"record","name":"DataFrame","namespace":"org.dflib","fields":[\
                {"name":"t","type":[{"type":"long","logicalType":"time-micros"},"null"]}]}""", schema.toString());
    }

    @Test
    public void decimalPrecision_Default() {

        DataFrame df = DataFrame.byColumn("d").of(Series.of(new BigDecimal("123.456"), new BigDecimal("1.4567")));
        Schema schema = new AvroSchemaCompiler().compileSchema(df);

        Assertions.assertNotNull(schema);
        assertEquals("""
                {"type":"record","name":"DataFrame","namespace":"org.dflib","fields":[\
                {"name":"d","type":[{"type":"bytes","logicalType":"big-decimal"},"null"]}]}""", schema.toString());
    }

    public enum _TestEnum {
        m, _x, V
    }
}
