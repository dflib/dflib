package org.dflib.parquet;

import org.apache.parquet.io.api.Binary;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class Load_PrimitiveTypesTest {

    @TempDir
    static Path outBase;

    // per https://parquet.apache.org/docs/file-format/types/
    static final String SCHEMA = """
            message test_schema {
               required boolean b;
               required int32 i;
               required int64 l;
               required float f;
               required double d;
               required binary bytes;
               required fixed_len_byte_array(3) bytesFixed;
             }
            """;

    public record R1(boolean b, int i, long l, float f, double d, byte[] bytes, byte[] bytesFixed) {
    }

    static Path createParquetFile() {
        return TestWriter.of(R1.class, outBase)
                .schema(SCHEMA)
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("b", 0);
                    c.addBoolean(r.b());
                    c.endField("b", 0);

                    c.startField("i", 1);
                    c.addInteger(r.i());
                    c.endField("i", 1);

                    c.startField("l", 2);
                    c.addLong(r.l());
                    c.endField("l", 2);

                    c.startField("f", 3);
                    c.addFloat(r.f());
                    c.endField("f", 3);

                    c.startField("d", 4);
                    c.addDouble(r.d());
                    c.endField("d", 4);

                    c.startField("bytes", 5);
                    c.addBinary(Binary.fromConstantByteArray(r.bytes()));
                    c.endField("bytes", 5);

                    c.startField("bytesFixed", 6);
                    c.addBinary(Binary.fromConstantByteArray(r.bytesFixed()));
                    c.endField("bytesFixed", 6);

                    c.endMessage();
                })
                .write(new R1(true, 6, 896L, 8.11f, 909.01d, new byte[]{50}, new byte[]{50, 51, 52}),
                        new R1(false, 8, -196L, -3.12f, -13.01d, new byte[]{60, 61}, new byte[]{50, 51, 52}));
    }

    @Test
    public void load() {
        DataFrame df = Parquet.load(createParquetFile());

        new DataFrameAsserts(df, "b", "i", "l", "f", "d", "bytes", "bytesFixed")
                .expectHeight(2)
                .expectRow(0, true, 6, 896L, 8.11f, 909.01d, new byte[]{50}, new byte[]{50, 51, 52})
                .expectRow(1, false, 8, -196L, -3.12f, -13.01d, new byte[]{60, 61}, new byte[]{50, 51, 52});
    }
}
