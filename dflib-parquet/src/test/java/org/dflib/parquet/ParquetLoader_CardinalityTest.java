package org.dflib.parquet;

import org.apache.parquet.io.api.Binary;
import org.dflib.DataFrame;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.UUID;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParquetLoader_CardinalityTest {

    @TempDir
    static Path outBase;

    static Path testFile;
    static Path noDictionaryFile;

    record R(Integer a, String b, BigDecimal c, Boolean d, Long e, long f) {
    }

    @BeforeAll
    static void createParquetFile() {
        testFile = write(true);
        noDictionaryFile = write(false);
    }

    static Path write(boolean dictionaryEncoding) {

        return TestWriter.of(R.class, outBase)
                .withDictionaryEncoding(dictionaryEncoding)
                .schema("""
                        message test_schema {
                            optional int32 a;
                            optional binary b (STRING);
                            optional int64 c (DECIMAL(18,2));
                            optional boolean d;
                            optional int64 e;
                            required int64 f;
                        }""")
                .writer((c, r) -> {
                    c.startMessage();

                    if (r.a() != null) {
                        c.startField("a", 0);
                        c.addInteger(r.a());
                        c.endField("a", 0);
                    }

                    if (r.b() != null) {
                        c.startField("b", 1);
                        c.addBinary(Binary.fromString(r.b()));
                        c.endField("b", 1);
                    }

                    if (r.c() != null) {
                        c.startField("c", 2);
                        c.addLong(TestEncoder.decimalToLong(r.c(), 2));
                        c.endField("c", 2);
                    }

                    if (r.d() != null) {
                        c.startField("d", 3);
                        c.addBoolean(r.d());
                        c.endField("d", 3);
                    }

                    if (r.e() != null) {
                        c.startField("e", 4);
                        c.addLong(r.e());
                        c.endField("e", 4);
                    }

                    c.startField("f", 5);
                    c.addLong(r.f());
                    c.endField("f", 5);

                    c.endMessage();
                })
                // using large enough values for Long and Integer so that Long.valueOf(..) and Integer.valueOf(..) cache is bypassed
                .write(
                        new R(1, "ab", new BigDecimal("609.1"), true, null, 0L),
                        new R(40000, "ab", new BigDecimal("12.6"), false, 66L, 66L),
                        new R(40000, "bc", new BigDecimal("609.1"), true, 66L, 66L),
                        new R(30000, "bc", new BigDecimal("12.6"), null, 68_000L, 68_000L),
                        new R(30000, null, new BigDecimal("609.1"), true, -66_000L, -66_000L),
                        new R(null, "bc", new BigDecimal("609.1"), true, -66_000L, -66_000L)
                );
    }

    @Test
    public void defaultCardinality() {
        DataFrame df = Parquet.loader().load(testFile);

        new DataFrameAsserts(df, "a", "b", "c", "d", "e", "f")
                .expectHeight(6)
                .expectRow(0, 1, "ab", new BigDecimal("609.10"), true, null, 0L)
                .expectRow(1, 40000, "ab", new BigDecimal("12.60"), false, 66L, 66L)
                .expectRow(2, 40000, "bc", new BigDecimal("609.10"), true, 66L, 66L)
                .expectRow(3, 30000, "bc", new BigDecimal("12.60"), null, 68_000L, 68_000L)
                .expectRow(4, 30000, null, new BigDecimal("609.10"), true, -66_000L, -66_000L)
                .expectRow(5, null, "bc", new BigDecimal("609.10"), true, -66_000L, -66_000L);

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode),
                $col("c").mapVal(System::identityHashCode),
                $col("d").mapVal(System::identityHashCode),
                $col("e").mapVal(System::identityHashCode),
                $col("f").mapVal(System::identityHashCode));

        // Everything should be compacted by default, except primitive columns. And smaller primitive will show lower
        // cardinality when boxed to objects due to Java itself caching smaller values

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
        assertEquals(2, idCardinality.getColumn(2).unique().size());
        assertEquals(3, idCardinality.getColumn(3).unique().size());
        assertEquals(4, idCardinality.getColumn(4).unique().size());
        assertEquals(5, idCardinality.getColumn(5).unique().size());
    }

    @Test
    public void noDictionaryNoCompaction() {

        // A column the writer left undictionaried is one it found nothing to deduplicate in, so the loader reads it
        // straight through rather than paying for a compaction cache. Values must of course be identical either way.

        DataFrame df = Parquet.loader().load(noDictionaryFile);

        new DataFrameAsserts(df, "a", "b", "c", "d", "e", "f")
                .expectHeight(6)
                .expectRow(0, 1, "ab", new BigDecimal("609.10"), true, null, 0L)
                .expectRow(1, 40000, "ab", new BigDecimal("12.60"), false, 66L, 66L)
                .expectRow(2, 40000, "bc", new BigDecimal("609.10"), true, 66L, 66L)
                .expectRow(3, 30000, "bc", new BigDecimal("12.60"), null, 68_000L, 68_000L)
                .expectRow(4, 30000, null, new BigDecimal("609.10"), true, -66_000L, -66_000L)
                .expectRow(5, null, "bc", new BigDecimal("609.10"), true, -66_000L, -66_000L);

        // BigDecimal has no JVM-wide instance cache of its own, so its identity count is a clean signal: 2 distinct
        // values shared when compacted, one instance per row when not
        DataFrame ids = df.cols().select($col("c").mapVal(System::identityHashCode));
        assertEquals(6, ids.getColumn(0).unique().size());

        DataFrame compacted = Parquet.loader().load(testFile).cols()
                .select($col("c").mapVal(System::identityHashCode));
        assertEquals(2, compacted.getColumn(0).unique().size());
    }

    @Test
    public void batchCardinality() {

        // The batch path shares instances the same way the row path does, but arrives there differently: instead of
        // decoding every row and then deduplicating the results, it decodes each distinct physical value once and
        // hands the same instance to every row repeating it.

        DataFrame df = new ParquetLoader(0).load(testFile);

        new DataFrameAsserts(df, "a", "b", "c", "d", "e", "f")
                .expectHeight(6)
                .expectRow(0, 1, "ab", new BigDecimal("609.10"), true, null, 0L)
                .expectRow(1, 40000, "ab", new BigDecimal("12.60"), false, 66L, 66L)
                .expectRow(2, 40000, "bc", new BigDecimal("609.10"), true, 66L, 66L)
                .expectRow(3, 30000, "bc", new BigDecimal("12.60"), null, 68_000L, 68_000L)
                .expectRow(4, 30000, null, new BigDecimal("609.10"), true, -66_000L, -66_000L)
                .expectRow(5, null, "bc", new BigDecimal("609.10"), true, -66_000L, -66_000L);

        DataFrame ids = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("c").mapVal(System::identityHashCode));

        assertEquals(4, ids.getColumn(0).unique().size());
        assertEquals(2, ids.getColumn(1).unique().size());
    }

    @Test
    public void batchNoDictionaryNoSharing() {

        DataFrame df = new ParquetLoader(0).load(noDictionaryFile);

        new DataFrameAsserts(df, "a", "b", "c", "d", "e", "f")
                .expectHeight(6)
                .expectRow(0, 1, "ab", new BigDecimal("609.10"), true, null, 0L)
                .expectRow(1, 40000, "ab", new BigDecimal("12.60"), false, 66L, 66L)
                .expectRow(2, 40000, "bc", new BigDecimal("609.10"), true, 66L, 66L)
                .expectRow(3, 30000, "bc", new BigDecimal("12.60"), null, 68_000L, 68_000L)
                .expectRow(4, 30000, null, new BigDecimal("609.10"), true, -66_000L, -66_000L)
                .expectRow(5, null, "bc", new BigDecimal("609.10"), true, -66_000L, -66_000L);

        DataFrame ids = df.cols().select($col("c").mapVal(System::identityHashCode));
        assertEquals(6, ids.getColumn(0).unique().size());
    }

    @Test
    public void cardinality_ByteArrayBackedDecimal() {

        // A decimal wider than 18 digits is FIXED_LEN_BYTE_ARRAY-backed, so it is keyed on the value's bytes rather
        // than on a primitive - on the batch path straight out of the shared buffer, on the row path out of the
        // byte[] Hardwood would have allocated on its way to the BigDecimal anyway

        DataFrame df = DataFrame.foldByRow("a").of(
                new BigDecimal("1.50"), new BigDecimal("-2.25"), new BigDecimal("1.50"),
                new BigDecimal("-2.25"), null, new BigDecimal("1.50"));

        Path file = outBase.resolve("wideDecimal.parquet");
        Parquet.saver().decimalSize(20, 2).save(df, file);

        for (DataFrame loaded : new DataFrame[]{new ParquetLoader(0).load(file), Parquet.loader().load(file)}) {

            new DataFrameAsserts(loaded, "a")
                    .expectHeight(6)
                    .expectRow(0, new BigDecimal("1.50"))
                    .expectRow(1, new BigDecimal("-2.25"))
                    .expectRow(2, new BigDecimal("1.50"))
                    .expectRow(3, new BigDecimal("-2.25"))
                    .expectRow(4, (Object) null)
                    .expectRow(5, new BigDecimal("1.50"));

            // 2 distinct values plus null
            DataFrame ids = loaded.cols().select($col("a").mapVal(System::identityHashCode));
            assertEquals(3, ids.getColumn(0).unique().size());
        }
    }

    @Test
    public void cardinality_Uuid() {

        // UUID is FIXED_LEN_BYTE_ARRAY(16)-backed, and keyed on its bytes on both paths

        UUID u1 = UUID.fromString("d3f1b0a4-1111-4a2b-8c3d-000000000001");
        UUID u2 = UUID.fromString("d3f1b0a4-2222-4a2b-8c3d-000000000002");

        DataFrame df = DataFrame.foldByRow("a").of(u1, u2, u1, u2, null, u1);

        Path file = outBase.resolve("uuid.parquet");
        Parquet.saver().save(df, file);

        for (DataFrame loaded : new DataFrame[]{new ParquetLoader(0).load(file), Parquet.loader().load(file)}) {

            new DataFrameAsserts(loaded, "a")
                    .expectHeight(6)
                    .expectRow(0, u1)
                    .expectRow(1, u2)
                    .expectRow(2, u1)
                    .expectRow(3, u2)
                    .expectRow(4, (Object) null)
                    .expectRow(5, u1);

            DataFrame ids = loaded.cols().select($col("a").mapVal(System::identityHashCode));
            assertEquals(3, ids.getColumn(0).unique().size());
        }
    }
}
