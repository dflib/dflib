package org.dflib.parquet;

import org.apache.parquet.io.api.Binary;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParquetLoader_CardinalityTest {

    @TempDir
    static Path outBase;

    static Path testFile;

    @BeforeAll
    static void createParquetFile() {
        record R(Integer a, String b, BigDecimal c, Boolean d, Long e, long f) {
        }

        testFile = TestWriter.of(R.class, outBase)
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
        DataFrame df = new ParquetLoader().load(testFile);

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

        // Converters using dictionaries will internally compact values, so we'd see lower cardinality
        assertEquals(6, idCardinality.getColumn(0).unique().size());
        assertEquals(6, idCardinality.getColumn(1).unique().size());
        assertEquals(6, idCardinality.getColumn(2).unique().size());

        // Boolean has fixed cardinality
        assertEquals(3, idCardinality.getColumn(3).unique().size());

        // Long (and Integer) columns cardinality is reduced due to Java Long.valueOf(..) caching of small values
        assertEquals(5, idCardinality.getColumn(4).unique().size());
        assertEquals(5, idCardinality.getColumn(5).unique().size());
    }

    @Test
    public void compactCardinality() {
        DataFrame df = new ParquetLoader()
                .compactCol("a")
                .compactCol("b")
                .compactCol("c")
                .compactCol("d")
                .compactCol("e")
                .compactCol("f")
                .load(testFile);

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

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
        assertEquals(2, idCardinality.getColumn(2).unique().size());
        assertEquals(3, idCardinality.getColumn(3).unique().size());
        assertEquals(4, idCardinality.getColumn(4).unique().size());

        // primitive columns are resolved without DFLib cache, only using Java Long.valueOf(..) cache for smaller values
        assertEquals(5, idCardinality.getColumn(5).unique().size());
    }
}
