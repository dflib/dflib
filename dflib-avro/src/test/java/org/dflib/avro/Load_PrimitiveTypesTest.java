package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class Load_PrimitiveTypesTest {

    @TempDir
    static Path outBase;

    // per https://avro.apache.org/docs/1.12.0/specification/#primitive-types
    static final String SCHEMA = """
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
            }""";

    public record R1(boolean b, int i, long l, float f, double d, byte[] bytes, String s) {
    }

    static Path createAvroFile() {

        return TestAvroWriter.of(R1.class, outBase)
                .schema(SCHEMA)
                .writer((r, o) -> {
                    r.put("nl", null);
                    r.put("b", o.b());
                    r.put("i", o.i());
                    r.put("l", o.l());
                    r.put("f", o.f());
                    r.put("d", o.d());
                    r.put("bytes", java.nio.ByteBuffer.wrap(o.bytes()));
                    r.put("s", o.s());
                })
                .write(new R1(true, 6, 896L, 8.11f, 909.01d, new byte[]{50, 51, 52}, "s1"),
                        new R1(false, 8, -196L, -3.12f, -13.01d, new byte[]{60, 61, 62}, "s2"));
    }

    @Test
    public void load() {
        DataFrame df = Avro.load(createAvroFile());

        new DataFrameAsserts(df, "nl", "b", "i", "l", "f", "d", "bytes", "s")
                .expectHeight(2)
                .expectRow(0, null, true, 6, 896L, 8.11f, 909.01d, new byte[]{50, 51, 52}, "s1")
                .expectRow(1, null, false, 8, -196L, -3.12f, -13.01d, new byte[]{60, 61, 62}, "s2");
    }
}
