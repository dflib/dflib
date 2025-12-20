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
        record R(Integer a, String b, BigDecimal c) {
        }

        testFile = TestWriter.of(R.class, outBase)
                .schema("""
                        message test_schema { 
                            optional int32 a;
                            optional binary b (STRING);
                            optional int64 c (DECIMAL(18,2)); 
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

                    c.endMessage();
                })
                .write(
                        new R(1, "ab", new BigDecimal("609.1")),
                        new R(40000, "ab", new BigDecimal("12.6")),
                        new R(40000, "bc", new BigDecimal("609.1")),
                        new R(30000, "bc", new BigDecimal("12.6")),
                        new R(30000, null, new BigDecimal("609.1")),
                        new R(null, "bc", new BigDecimal("609.1"))
                );
    }

    @Test
    public void valueCardinality() {
        DataFrame df = new ParquetLoader().load(testFile);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(6)
                .expectRow(0, 1, "ab", new BigDecimal("609.10"))
                .expectRow(1, 40000, "ab", new BigDecimal("12.60"))
                .expectRow(2, 40000, "bc", new BigDecimal("609.10"))
                .expectRow(3, 30000, "bc", new BigDecimal("12.60"))
                .expectRow(4, 30000, null, new BigDecimal("609.10"))
                .expectRow(5, null, "bc", new BigDecimal("609.10"));

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode),
                $col("c").mapVal(System::identityHashCode));

        assertEquals(6, idCardinality.getColumn(0).unique().size());

        // Converters using dictionaries will internally compact values, so we'd see lower cardinality
        assertEquals(3, idCardinality.getColumn(1).unique().size());
        assertEquals(2, idCardinality.getColumn(2).unique().size());
    }

    @Test
    public void valueCardinality_compactCol_Name() {
        DataFrame df = new ParquetLoader()
                .compactCol("a")
                .compactCol("b")
                .load(testFile);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(6)
                .expectRow(0, 1, "ab", new BigDecimal("609.10"))
                .expectRow(1, 40000, "ab", new BigDecimal("12.60"))
                .expectRow(2, 40000, "bc", new BigDecimal("609.10"))
                .expectRow(3, 30000, "bc", new BigDecimal("12.60"))
                .expectRow(4, 30000, null, new BigDecimal("609.10"))
                .expectRow(5, null, "bc", new BigDecimal("609.10"));

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode),
                $col("c").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
        assertEquals(2, idCardinality.getColumn(2).unique().size());
    }
}
