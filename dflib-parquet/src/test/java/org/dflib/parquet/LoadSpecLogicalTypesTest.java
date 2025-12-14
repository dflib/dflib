package org.dflib.parquet;

import org.apache.parquet.io.api.Binary;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.UUID;

public class LoadSpecLogicalTypesTest {

    @TempDir
    static Path outBase;

    // per https://parquet.apache.org/docs/file-format/types/logicaltypes/
    // TODO: ENUM, DATE, TIME, TIMESTAMP, DECIMAL, etc.

    @Test
    public void string() {

        record RS(String s) {
        }

        Path p = TestParquetWriter.of(RS.class, outBase)
                .schema("message test_schema { required binary s (STRING); }")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("s", 0);
                    c.addBinary(Binary.fromString(r.s()));
                    c.endField("s", 0);

                    c.endMessage();
                })
                .write(new RS("s1"), new RS("s2"));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "s")
                .expectHeight(2)
                .expectRow(0, "s1")
                .expectRow(1, "s2");
    }

    @Test
    public void uuid() {

        record RU(UUID u) {
        }

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        Path p = TestParquetWriter.of(RU.class, outBase)
                .schema("message test_schema { required fixed_len_byte_array(16) u (UUID); }")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("u", 0);
                    c.addBinary(Binary.fromConstantByteArray(uuidToBytes(r.u())));
                    c.endField("u", 0);

                    c.endMessage();
                })
                .write(new RU(uuid1), new RU(uuid2));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "u")
                .expectHeight(2)
                .expectRow(0, uuid1)
                .expectRow(1, uuid2);
    }

    @Test
    public void intSigned() {

        record RIS(byte bt, short sh, int i, long l) {
        }

        // TODO: per https://parquet.apache.org/docs/file-format/types/logicaltypes/ , this notation is deprecated.
        //  INT_8 -> INT(8, true). The new notation is not supported as of Parquet 1.15.2
        Path p = TestParquetWriter.of(RIS.class, outBase)
                .schema("""
                        message test_schema {
                           required int32 bt (INT_8);
                           required int32 sh (INT_16);
                           required int32 i (INT_32);
                           required int64 l (INT_64);
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("bt", 0);
                    c.addInteger(r.bt());
                    c.endField("bt", 0);

                    c.startField("sh", 1);
                    c.addInteger(r.sh());
                    c.endField("sh", 1);

                    c.startField("i", 2);
                    c.addInteger(r.i());
                    c.endField("i", 2);

                    c.startField("l", 3);
                    c.addLong(r.l());
                    c.endField("l", 3);

                    c.endMessage();
                })
                .write(new RIS((byte) 101, (short) 300, 1000, 10_001L),
                        new RIS((byte) -101, (short) -300, -1000, -10_001L));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "bt", "sh", "i", "l")
                .expectHeight(2)
                .expectRow(0, (byte) 101, (short) 300, 1000, 10_001L)
                .expectRow(1, (byte) -101, (short) -300, -1000, -10_001L);
    }

    @Test
    @Disabled("Why does it succeed with negative numbers?") // TODO
    public void intUnsigned() {

        record RIU(byte ubt, short ush, int ui, long ul) {
        }

        // TODO: per https://parquet.apache.org/docs/file-format/types/logicaltypes/ , this notation is deprecated.
        //  UINT_8 -> INT(8, false). The new notation is not supported as of Parquet 1.15.2
        Path p = TestParquetWriter.of(RIU.class, outBase)
                .schema("""
                        message test_schema {
                           required int32 ubt (UINT_8);
                           required int32 ush (UINT_16);
                           required int32 ui (UINT_32);
                           required int64 ul (UINT_64);
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("ubt", 0);
                    c.addInteger(r.ubt());
                    c.endField("ubt", 0);

                    c.startField("ush", 1);
                    c.addInteger(r.ush());
                    c.endField("ush", 1);

                    c.startField("ui", 2);
                    c.addInteger(r.ui());
                    c.endField("ui", 2);

                    c.startField("ul", 3);
                    c.addLong(r.ul());
                    c.endField("ul", 3);

                    c.endMessage();
                })
                .write(new RIU((byte) 101, (short) 300, 1000, 10_001L),
                        new RIU((byte) -101, (short) -300, -1000, -10_001L));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "ubt", "ush", "ui", "ul")
                .expectHeight(2)
                .expectRow(0, (byte) 101, (short) 300, 1000, 10_001L)
                .expectRow(1, (byte) -101, (short) -300, -1000, -10_001L);
    }

    private static byte[] uuidToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();
        appendInt((int) (hi >> 32), bytes, 0);
        appendInt((int) hi, bytes, 4);
        appendInt((int) (lo >> 32), bytes, 8);
        appendInt((int) lo, bytes, 12);
        return bytes;
    }

    private static void appendInt(int value, byte[] bytes, int offset) {
        bytes[offset] = (byte) (value >> 24);
        bytes[++offset] = (byte) (value >> 16);
        bytes[++offset] = (byte) (value >> 8);
        bytes[++offset] = (byte) value;
    }
}
