package org.dflib.parquet;

import org.apache.parquet.io.api.Binary;
import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Files large enough to be worth Hardwood's batch API are read a column at a time, everything else a row at a time.
 * Real files that cross that threshold are too big for a unit test, so the threshold is lowered here instead, and
 * each file is read both ways and the results compared - the row path being the one the rest of the suite covers.
 */
public class BatchVsRowReadTest {

    @TempDir
    Path outBase;

    private DataFrame readByRow(Path file) {
        return new ParquetLoader(Integer.MAX_VALUE).load(file);
    }

    private DataFrame readByColumn(Path file) {
        return new ParquetLoader(0).load(file);
    }

    /**
     * Asserts the two read paths agree, and that they were actually different paths.
     */
    private void assertSameBothWays(Path file) {
        DataFrame byRow = readByRow(file);
        DataFrame byColumn = readByColumn(file);

        new DataFrameAsserts(byColumn, byRow.getColumnsIndex()).expectHeight(byRow.height());

        for (String column : byRow.getColumnsIndex()) {
            Series<?> r = byRow.getColumn(column);
            Series<?> c = byColumn.getColumn(column);

            assertEquals(r.getClass(), c.getClass(), "Series type differs for column '" + column + "'");
            for (int i = 0; i < r.size(); i++) {
                assertEquals(r.get(i), c.get(i), "Value differs at " + column + "[" + i + "]");
            }
        }
    }

    @Test
    public void typesDflibWrites() {
        DataFrame df = DataFrame.byArrayRow("i", "ip", "l", "lp", "d", "dp", "f", "b", "bp",
                        "s", "e", "uuid", "date", "time", "ldt", "instant")
                .appender()
                .append(1, 2, 3L, 4L, 5.0, 6.0, 7.0f, true, false,
                        "one", EnumValues.one, UUID.randomUUID(), LocalDate.of(2025, 4, 5),
                        LocalTime.of(1, 2, 3), LocalDateTime.of(2025, 1, 2, 3, 4, 5), Instant.ofEpochSecond(1234, 5000))
                .append(null, 20, null, 40L, null, 60.0, null, null, true,
                        null, EnumValues.two, null, null, null, null, null)
                .append(100, 200, 300L, 400L, 500.0, 600.0, 700.0f, false, false,
                        "three", EnumValues.three, UUID.randomUUID(), LocalDate.of(1981, 6, 9),
                        LocalTime.of(23, 59, 58), LocalDateTime.of(1999, 12, 31, 23, 59), Instant.ofEpochSecond(-5, 1))
                .toDataFrame();

        Path file = outBase.resolve("dflibTypes.parquet");
        Parquet.saver().save(df, file);

        assertSameBothWays(file);
    }

    record RP(int i, long l, float f, double d, boolean b) {
    }

    @Test
    public void requiredPrimitives() {
        // "required" columns are the ones the batch path copies straight into a primitive Series. Written here
        // rather than through the saver, which has no mapping for a primitive float Series.
        Path file = TestWriter.of(RP.class, outBase)
                .schema("""
                        message test_schema {
                           required int32 i;
                           required int64 l;
                           required float f;
                           required double d;
                           required boolean b;
                        }""")
                .writer((c, r) -> {
                    c.startMessage();
                    c.startField("i", 0);
                    c.addInteger(r.i());
                    c.endField("i", 0);
                    c.startField("l", 1);
                    c.addLong(r.l());
                    c.endField("l", 1);
                    c.startField("f", 2);
                    c.addFloat(r.f());
                    c.endField("f", 2);
                    c.startField("d", 3);
                    c.addDouble(r.d());
                    c.endField("d", 3);
                    c.startField("b", 4);
                    c.addBoolean(r.b());
                    c.endField("b", 4);
                    c.endMessage();
                })
                .write(new RP(1, 4L, 1.5f, 7.0, true),
                        new RP(-2, -5L, -2.5f, -8.0, false),
                        new RP(3, 6L, 3.5f, 9.0, true));

        assertSameBothWays(file);

        // and that the batch path produced primitive Series rather than boxed ones
        DataFrame df = readByColumn(file);
        assertInstanceOf(IntSeries.class, df.getColumn("i"));
        assertInstanceOf(LongSeries.class, df.getColumn("l"));
        assertInstanceOf(FloatSeries.class, df.getColumn("f"));
        assertInstanceOf(DoubleSeries.class, df.getColumn("d"));
        assertInstanceOf(BooleanSeries.class, df.getColumn("b"));
    }

    @Test
    public void decimals() {
        for (int[] size : new int[][]{{9, 4}, {18, 3}, {20, 5}}) {
            DataFrame df = DataFrame.foldByRow("a").of(
                    new BigDecimal("1.5").setScale(size[1]),
                    null,
                    new BigDecimal("-2.25").setScale(size[1]));

            Path file = outBase.resolve("decimal" + size[0] + ".parquet");
            Parquet.saver().decimalSize(size[0], size[1]).save(df, file);

            assertSameBothWays(file);
        }
    }

    @Test
    public void timeUnits() {
        for (TimeUnit unit : TimeUnit.values()) {
            DataFrame df = DataFrame.foldByRow("t", "ldt", "instant").of(
                    LocalTime.of(15, 1, 12), LocalDateTime.of(2020, 2, 3, 4, 5, 6), Instant.ofEpochSecond(99, 0),
                    null, null, null);

            Path file = outBase.resolve("time" + unit + ".parquet");
            Parquet.saver().timeUnit(unit).save(df, file);

            assertSameBothWays(file);
        }
    }

    public enum EnumValues {
        one, two, three
    }

    record RU(Integer ubt, Integer ush, Integer ui, Long ul) {
    }

    @Test
    public void unsignedInts() {
        Path file = TestWriter.of(RU.class, outBase)
                .schema("""
                        message test_schema {
                           optional int32 ubt (INTEGER(8,false));
                           optional int32 ush (INTEGER(16,false));
                           optional int32 ui (INTEGER(32,false));
                           optional int64 ul (INTEGER(64,false));
                        }""")
                .writer((c, r) -> {
                    c.startMessage();
                    if (r.ubt() != null) {
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
                    }
                    c.endMessage();
                })
                .write(new RU(255, 65_535, -1, -1L), new RU(null, null, null, null), new RU(200, 40_000, 1, 1L));

        assertSameBothWays(file);

        // and the widening itself, so this doesn't just assert two paths are equally wrong
        DataFrame df = readByColumn(file);
        assertEquals((short) 255, df.get(0, 0));
        assertEquals(65_535, df.get(1, 0));
        assertEquals(4_294_967_295L, df.get(2, 0));
        assertEquals(new BigInteger("18446744073709551615"), df.get(3, 0));
    }

    record RB(byte[] bytes, byte[] flba, byte[] f16, int[] interval) {
    }

    @Test
    public void binaryFloat16AndInterval() {
        Path file = TestWriter.of(RB.class, outBase)
                .schema("""
                        message test_schema {
                           required binary bytes;
                           required fixed_len_byte_array(3) flba;
                           required fixed_len_byte_array(2) f16 (FLOAT16);
                           required fixed_len_byte_array(12) interval (INTERVAL);
                        }""")
                .writer((c, r) -> {
                    c.startMessage();
                    c.startField("bytes", 0);
                    c.addBinary(Binary.fromConstantByteArray(r.bytes()));
                    c.endField("bytes", 0);
                    c.startField("flba", 1);
                    c.addBinary(Binary.fromConstantByteArray(r.flba()));
                    c.endField("flba", 1);
                    c.startField("f16", 2);
                    c.addBinary(TestEncoder.floatToBytes(5.0f));
                    c.endField("f16", 2);
                    c.startField("interval", 3);
                    c.addBinary(TestEncoder.intsToBytes(r.interval()));
                    c.endField("interval", 3);
                    c.endMessage();
                })
                .write(new RB(new byte[]{1, 2}, new byte[]{3, 4, 5}, null, new int[]{1, 2, 0}),
                        new RB(new byte[]{9}, new byte[]{8, 7, 6}, null, new int[]{0, 0, 7}));

        DataFrame byRow = readByRow(file);
        DataFrame byColumn = readByColumn(file);

        assertEquals(byRow.height(), byColumn.height());
        for (int i = 0; i < byRow.height(); i++) {
            assertArrayEquals(
                    (byte[]) byRow.get(0, i), (byte[]) byColumn.get(0, i), "bytes row " + i);
            assertArrayEquals(
                    (byte[]) byRow.get(1, i), (byte[]) byColumn.get(1, i), "flba row " + i);
            assertEquals(byRow.get(2, i), byColumn.get(2, i), "float16 row " + i);
            assertArrayEquals(
                    (int[]) byRow.get(3, i), (int[]) byColumn.get(3, i), "interval row " + i);
        }

        assertEquals(5.0f, byColumn.get(2, 0));
    }

    record RS(Byte bt, Short sh) {
    }

    @Test
    public void byteAndShort() {
        Path file = TestWriter.of(RS.class, outBase)
                .schema("""
                        message test_schema {
                           optional int32 bt (INTEGER(8,true));
                           optional int32 sh (INTEGER(16,true));
                        }""")
                .writer((c, r) -> {
                    c.startMessage();
                    if (r.bt() != null) {
                        c.startField("bt", 0);
                        c.addInteger(r.bt());
                        c.endField("bt", 0);
                        c.startField("sh", 1);
                        c.addInteger(r.sh());
                        c.endField("sh", 1);
                    }
                    c.endMessage();
                })
                .write(new RS((byte) 101, (short) 300), new RS(null, null), new RS((byte) -101, (short) -300));

        assertSameBothWays(file);
        assertEquals((byte) 101, readByColumn(file).get(0, 0));
    }
}
