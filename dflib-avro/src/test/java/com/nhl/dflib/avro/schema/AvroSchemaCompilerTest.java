package com.nhl.dflib.avro.schema;

import com.nhl.dflib.*;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvroSchemaCompilerTest {

    static final DataFrame df = DataFrame.newFrame(
            "int", "Integer", "long", "Long", "double", "Double", "bool", "Bool", "String", "Nulls")
            .columns(
                    IntSeries.forInts(1, 3),
                    Series.forData(11, null),
                    LongSeries.forLongs(4L, 5L),
                    Series.forData(21L, null),
                    DoubleSeries.forDoubles(20.12, 20.1235),
                    Series.forData(30.1, null),
                    BooleanSeries.forBooleans(true, false),
                    Series.forData(Boolean.TRUE, null),
                    Series.forData("s1", null),
                    Series.forData(null, null)
            );

    @Test
    public void testCompileSchema() {
        Schema schema = new AvroSchemaCompiler()
                .name("s")
                .namespace("com.foo")
                .compileSchema(df);

        Assertions.assertNotNull(schema);
        assertEquals("{\"type\":\"record\",\"name\":\"s\",\"namespace\":\"com.foo\",\"fields\":[" +
                "{\"name\":\"int\",\"type\":\"int\"}," +
                "{\"name\":\"Integer\",\"type\":[\"int\",\"null\"]}," +
                "{\"name\":\"long\",\"type\":\"long\"}," +
                "{\"name\":\"Long\",\"type\":[\"long\",\"null\"]}," +
                "{\"name\":\"double\",\"type\":\"double\"}," +
                "{\"name\":\"Double\",\"type\":[\"double\",\"null\"]}," +
                "{\"name\":\"bool\",\"type\":\"boolean\"}," +
                "{\"name\":\"Bool\",\"type\":[\"boolean\",\"null\"]}," +
                "{\"name\":\"String\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"]}," +
                "{\"name\":\"Nulls\",\"type\":\"null\"}]}", schema.toString());
    }

    @Test
    public void testCompileSchema_EmptyDF() {

        DataFrame empty = DataFrame.newFrame("A", "B").empty();

        Schema schema = new AvroSchemaCompiler()
                .name("s")
                .namespace("com.foo")
                .compileSchema(empty);

        Assertions.assertNotNull(schema);
        assertEquals("{\"type\":\"record\",\"name\":\"s\",\"namespace\":\"com.foo\",\"fields\":[" +
                "{\"name\":\"A\",\"type\":[{\"type\":\"string\",\"logicalType\":\"dflib-unmapped\"},\"null\"]}," +
                "{\"name\":\"B\",\"type\":[{\"type\":\"string\",\"logicalType\":\"dflib-unmapped\"},\"null\"]}]}", schema.toString());
    }

    @Test
    public void testCompileSchema_DefaultNames() {

        DataFrame empty = DataFrame.newFrame("A").empty();
        Schema schema = new AvroSchemaCompiler().compileSchema(empty);

        Assertions.assertNotNull(schema);
        assertEquals("{\"type\":\"record\",\"name\":\"DataFrame\",\"namespace\":\"com.nhl.dflib\",\"fields\":[" +
                "{\"name\":\"A\",\"type\":[{\"type\":\"string\",\"logicalType\":\"dflib-unmapped\"},\"null\"]}]}", schema.toString());
    }

    @Test
    public void testEnumSchema() {

        Schema schema = new AvroSchemaCompiler().enumSchema(_TestEnum.class);

        Assertions.assertNotNull(schema);
        assertEquals("{\"type\":\"enum\"," +
                        "\"name\":\"_TestEnum\"," +
                        "\"doc\":\"com.nhl.dflib.avro.schema\"," +
                        "\"symbols\":[\"m\",\"_x\",\"V\"]," +
                        "\"dflib.enum.type\":\"com.nhl.dflib.avro.schema.AvroSchemaCompilerTest$_TestEnum\"}",
                schema.toString());
    }

    public enum _TestEnum {
        m, _x, V
    }
}
