package org.dflib.parquet;

import org.apache.parquet.io.api.Binary;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Load_PrimitiveLogicalTypesTest {

    @TempDir
    static Path outBase;

    // per https://parquet.apache.org/docs/file-format/types/logicaltypes/
    // TODO: embedded types - JSON, BSON, GEOMETRY, GEOGRAPHY

    @Test
    public void string() {

        record R(String s) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("message test_schema { required binary s (STRING); }")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("s", 0);
                    c.addBinary(Binary.fromString(r.s()));
                    c.endField("s", 0);

                    c.endMessage();
                })
                .write(new R("s1"), new R("s2"));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "s")
                .expectHeight(2)
                .expectRow(0, "s1")
                .expectRow(1, "s2");
    }

    @Test
    public void enumString() {

        record R(String s) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("message test_schema { required binary s (ENUM); }")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("s", 0);
                    c.addBinary(Binary.fromString(r.s()));
                    c.endField("s", 0);

                    c.endMessage();
                })
                .write(new R("s1"), new R("s2"));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "s")
                .expectHeight(2)
                .expectRow(0, "s1")
                .expectRow(1, "s2");
    }

    @Test
    public void uuid() {

        record R(UUID u) {
        }

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        Path p = TestWriter.of(R.class, outBase)
                .schema("message test_schema { required fixed_len_byte_array(16) u (UUID); }")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("u", 0);
                    c.addBinary(TestEncoder.uuidToBytes(r.u()));
                    c.endField("u", 0);

                    c.endMessage();
                })
                .write(new R(uuid1), new R(uuid2));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "u")
                .expectHeight(2)
                .expectRow(0, uuid1)
                .expectRow(1, uuid2);
    }

    @Test
    public void intSigned() {

        record R(byte bt, short sh, int i, long l) {
        }

        // TODO: per https://parquet.apache.org/docs/file-format/types/logicaltypes/ , this notation is deprecated.
        //  INT_8 -> INT(8, true). The new notation is not supported as of Parquet 1.15.2
        Path p = TestWriter.of(R.class, outBase)
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
                .write(new R((byte) 101, (short) 300, 1000, 10_001L),
                        new R((byte) -101, (short) -300, -1000, -10_001L));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "bt", "sh", "i", "l")
                .expectHeight(2)
                .expectRow(0, (byte) 101, (short) 300, 1000, 10_001L)
                .expectRow(1, (byte) -101, (short) -300, -1000, -10_001L);
    }

    @Test
    @Disabled("Why does it succeed with negative numbers?") // TODO
    public void intUnsigned() {

        record R(byte ubt, short ush, int ui, long ul) {
        }

        // TODO: per https://parquet.apache.org/docs/file-format/types/logicaltypes/ , this notation is deprecated.
        //  UINT_8 -> INT(8, false). The new notation is not supported as of Parquet 1.15.2
        Path p = TestWriter.of(R.class, outBase)
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
                .write(new R((byte) 101, (short) 300, 1000, 10_001L),
                        new R((byte) -101, (short) -300, -1000, -10_001L));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "ubt", "ush", "ui", "ul")
                .expectHeight(2)
                .expectRow(0, (byte) 101, (short) 300, 1000, 10_001L)
                .expectRow(1, (byte) -101, (short) -300, -1000, -10_001L);
    }

    @Test
    public void decimal() {

        record R(BigDecimal dint32, BigDecimal dint64, BigDecimal dfixedLenBytes, BigDecimal dbinary) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        message test_schema { 
                            required int32 dint32 (DECIMAL(9,2)); 
                            required int64 dint64 (DECIMAL(18,8)); 
                            required fixed_len_byte_array(12) dfixedLenBytes (DECIMAL(28,6)); 
                            required binary dbinary (DECIMAL(28,6));
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("dint32", 0);
                    c.addInteger(TestEncoder.decimalToInt(r.dint32(), 2));
                    c.endField("dint32", 0);

                    c.startField("dint64", 1);
                    c.addLong(TestEncoder.decimalToLong(r.dint64(), 8));
                    c.endField("dint64", 1);

                    c.startField("dfixedLenBytes", 2);
                    c.addBinary(TestEncoder.decimalToBytes(r.dfixedLenBytes(), 6, 12));
                    c.endField("dfixedLenBytes", 2);

                    c.startField("dbinary", 3);
                    c.addBinary(TestEncoder.decimalToBytes(r.dbinary(), 6, 12));
                    c.endField("dbinary", 3);

                    c.endMessage();
                })
                .write(new R(
                                new BigDecimal("67348.19"),
                                new BigDecimal("-873480976.1909876"),
                                new BigDecimal("973487657309221234.19987"),
                                new BigDecimal("973487657309221234.19987")),
                        new R(
                                new BigDecimal("57348.14"),
                                new BigDecimal("-376480976.2969876"),
                                new BigDecimal("43487657309221234.19988"),
                                new BigDecimal("673487657309221234.19989")));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "dint32", "dint64", "dfixedLenBytes", "dbinary")
                .expectHeight(2)
                .expectRow(0, new BigDecimal("67348.19"),
                        new BigDecimal("-873480976.19098760"),
                        new BigDecimal("973487657309221234.199870"),
                        new BigDecimal("973487657309221234.199870"))
                .expectRow(1, new BigDecimal("57348.14"),
                        new BigDecimal("-376480976.29698760"),
                        new BigDecimal("43487657309221234.199880"),
                        new BigDecimal("673487657309221234.199890"));
    }

    @Test
    public void float16() {

        record R(float f) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        message test_schema { 
                            required fixed_len_byte_array(2) f (FLOAT16); 
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("f", 0);
                    c.addBinary(TestEncoder.floatToBytes(r.f()));
                    c.endField("f", 0);

                    c.endMessage();
                })
                .write(new R(5.0f), new R(-2.1f));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "f")
                .expectHeight(2)
                .expectRow(0, 5.0f);

        // manually deal with float rounding
        assertEquals(-21, Math.round((float) df.get(0, 1) * 10));
    }

    @Test
    public void date() {

        record R(LocalDate d) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        message test_schema { 
                            required int32 d (DATE); 
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("d", 0);
                    c.addInteger((int) r.d().toEpochDay());
                    c.endField("d", 0);

                    c.endMessage();
                })
                .write(new R(LocalDate.of(2025, 4, 5)), new R(LocalDate.of(1981, 6, 9)));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "d")
                .expectHeight(2)
                .expectRow(0, LocalDate.of(2025, 4, 5))
                .expectRow(1, LocalDate.of(1981, 6, 9));
    }

    @Test
    public void timeMillis() {

        record R(LocalTime millis) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        message test_schema { 
                            optional int32 millis (TIME(MILLIS,false));
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("millis", 0);
                    c.addInteger((int) (r.millis().toNanoOfDay() / 1_000_000));
                    c.endField("millis", 0);

                    c.endMessage();
                })
                .write(new R(LocalTime.of(15, 1, 12, 150_000_000)),
                        new R(LocalTime.of(18, 2, 12, 25_000_000)));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "millis")
                .expectHeight(2)
                .expectRow(0, LocalTime.of(15, 1, 12, 150_000_000))
                .expectRow(1, LocalTime.of(18, 2, 12, 25_000_000));
    }

    @Test
    public void timeMacrosNanos() {

        record R(LocalTime micros, LocalTime nanos) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        message test_schema { 
                            optional int64 micros (TIME(MICROS,false));
                            optional int64 nanos (TIME(NANOS,false));
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("micros", 0);
                    c.addLong(r.micros().toNanoOfDay() / 1_000);
                    c.endField("micros", 0);

                    c.startField("nanos", 1);
                    c.addLong(r.nanos().toNanoOfDay());
                    c.endField("nanos", 1);

                    c.endMessage();
                })
                .write(new R(LocalTime.of(16, 2, 13, 16_002_002),
                                LocalTime.of(1, 0, 5, 17_003_003)),
                        new R(LocalTime.of(0, 3, 13, 26_002_002),
                                LocalTime.of(23, 59, 59, 27_003_003)));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "micros", "nanos")
                .expectHeight(2)
                .expectRow(0,
                        LocalTime.of(16, 2, 13, 16_002_000),
                        LocalTime.of(1, 0, 5, 17_003_003))
                .expectRow(1,
                        LocalTime.of(0, 3, 13, 26_002_000),
                        LocalTime.of(23, 59, 59, 27_003_003));
    }

    @Test
    public void timestampLocal() {

        record R(LocalDateTime millis, LocalDateTime micros, LocalDateTime nanos) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        message test_schema { 
                            optional int64 millis (TIMESTAMP(MILLIS,false));
                            optional int64 micros (TIMESTAMP(MICROS,false));
                            optional int64 nanos (TIMESTAMP(NANOS,false));
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("millis", 0);
                    c.addLong(r.millis().toEpochSecond(ZoneOffset.UTC) * 1_000L + (r.millis().getNano() / 1_000_000L));
                    c.endField("millis", 0);

                    c.startField("micros", 1);
                    c.addLong(r.micros().toEpochSecond(ZoneOffset.UTC) * 1_000_000L + (r.micros().getNano() / 1_000L));
                    c.endField("micros", 1);

                    c.startField("nanos", 2);
                    c.addLong(r.nanos().toEpochSecond(ZoneOffset.UTC) * 1_000_000_000L + r.nanos().getNano());
                    c.endField("nanos", 2);

                    c.endMessage();
                })
                .write(new R(LocalDateTime.of(2026, 1, 15, 16, 2, 13, 16_002_002),
                                LocalDateTime.of(2025, 1, 15, 16, 2, 13, 16_002_002),
                                LocalDateTime.of(2024, 1, 15, 1, 0, 5, 17_003_003)),
                        new R(LocalDateTime.of(2023, 1, 15, 0, 3, 13, 26_002_002),
                                LocalDateTime.of(2022, 1, 15, 0, 3, 13, 26_002_002),
                                LocalDateTime.of(2021, 1, 15, 23, 59, 59, 27_003_003)));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "millis", "micros", "nanos")
                .expectHeight(2)
                .expectRow(0,
                        LocalDateTime.of(2026, 1, 15, 16, 2, 13, 16_000_000),
                        LocalDateTime.of(2025, 1, 15, 16, 2, 13, 16_002_000),
                        LocalDateTime.of(2024, 1, 15, 1, 0, 5, 17_003_003))
                .expectRow(1,
                        LocalDateTime.of(2023, 1, 15, 0, 3, 13, 26_000_000),
                        LocalDateTime.of(2022, 1, 15, 0, 3, 13, 26_002_000),
                        LocalDateTime.of(2021, 1, 15, 23, 59, 59, 27_003_003));
    }

    @Test
    public void timestampInstant() {

        record R(Instant millis, Instant micros, Instant nanos) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        message test_schema { 
                            optional int64 millis (TIMESTAMP(MILLIS,true));
                            optional int64 micros (TIMESTAMP(MICROS,true));
                            optional int64 nanos (TIMESTAMP(NANOS,true));
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    c.startField("millis", 0);
                    c.addLong(r.millis().toEpochMilli());
                    c.endField("millis", 0);

                    c.startField("micros", 1);
                    c.addLong(r.micros().toEpochMilli() * 1_000L + (r.micros().getNano() % 1_000L));
                    c.endField("micros", 1);

                    c.startField("nanos", 2);
                    c.addLong(r.nanos().toEpochMilli() * 1_000_000L + r.nanos().getNano() % 1_000_000L);
                    c.endField("nanos", 2);

                    c.endMessage();
                })
                .write(new R(Instant.ofEpochSecond(1768492933, 16_002_002),
                                Instant.ofEpochSecond(1736956933, 16_002_002),
                                Instant.ofEpochSecond(1705334533, 17_003_003)),
                        new R(Instant.ofEpochSecond(1673798533, 26_002_002),
                                Instant.ofEpochSecond(1642262533, 26_002_002),
                                Instant.ofEpochSecond(1610726533, 27_003_003)));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "millis", "micros", "nanos")
                .expectHeight(2)
                .expectRow(0,
                        Instant.ofEpochSecond(1768492933, 16_000_000),
                        Instant.ofEpochSecond(1736956933, 16_002_000),
                        Instant.ofEpochSecond(1705334533, 17_003_003))
                .expectRow(1,
                        Instant.ofEpochSecond(1673798533, 26_000_000),
                        Instant.ofEpochSecond(1642262533, 26_002_000),
                        Instant.ofEpochSecond(1610726533, 27_003_003));
    }
}
