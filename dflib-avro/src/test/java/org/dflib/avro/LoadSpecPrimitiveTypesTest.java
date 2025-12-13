package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class LoadSpecPrimitiveTypesTest {

    @TempDir
    static Path outBase;

    // per https://avro.apache.org/docs/1.12.0/specification/#primitive-types
    static final Schema SCHEMA = new Schema.Parser().parse("""
            {
              "type":"record",
              "name":"DataFrame",
              "namespace":"org.dflib",
              "fields":[
                {"name":"nl","type":"null"},
                {"name":"b","type":"boolean"},
                {"name":"i","type":"int"},
                {"name":"l","type":"long"},
                {"name":"f","type":"float"},
                {"name":"d","type":"double"},
                {"name":"bytes","type":"bytes"},
                {"name":"s","type":"string"}
              ]
            }""");

    public record R1(boolean b, int i, long l, float f, double d, byte[] bytes, String s) {
    }

    static File createAvroFile() throws IOException {

        File out = new File(outBase.toFile(), "java.avro");
        List<R1> list = List.of(
                new R1(true, 6, 896L, 8.11f, 909.01d, new byte[]{50, 51, 52}, "s1"),
                new R1(false, 8, -196L, -3.12f, -13.01d, new byte[]{60, 61, 62}, "s2")
        );

        GenericData data = new GenericData();
        var datumWriter = new GenericDatumWriter<GenericRecord>(SCHEMA, data);

        try (var fileWriter = new DataFileWriter<>(datumWriter)) {
            fileWriter.create(SCHEMA, out);

            for (R1 o : list) {
                GenericRecord r = new GenericData.Record(SCHEMA);
                r.put("nl", null);
                r.put("b", o.b());
                r.put("i", o.i());
                r.put("l", o.l());
                r.put("f", o.f());
                r.put("d", o.d());
                r.put("bytes", java.nio.ByteBuffer.wrap(o.bytes()));
                r.put("s", o.s());
                fileWriter.append(r);
            }
        }

        return out;
    }

    @Test
    public void load() throws IOException {
        DataFrame df = Avro.load(createAvroFile());

        new DataFrameAsserts(df, "nl", "b", "i", "l", "f", "d", "bytes", "s")
                .expectHeight(2)
                .expectRow(0, null, true, 6, 896L, 8.11f, 909.01d, new byte[]{50, 51, 52}, "s1")
                .expectRow(1, null, false, 8, -196L, -3.12f, -13.01d, new byte[]{60, 61, 62}, "s2");
    }
}
