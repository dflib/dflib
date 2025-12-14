package org.dflib.parquet;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParquetLoader_CardinalityTest {

    static File _TEST1_PARQUET;

    @BeforeAll
    public static void setupSrcDirs() throws URISyntaxException {
        // the file is intentionally created with "parquet" tool, not DFLib
        _TEST1_PARQUET = new File(SaveThenLoadTest.class.getResource("test1.parquet").toURI()).getAbsoluteFile();
    }

    @Test
    public void valueCardinality() {
        DataFrame df = new ParquetLoader().load(_TEST1_PARQUET);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        // looks like Parquet interns deserialized Strings (but not integers or other types),
        // so String id cardinality is reduced by default
        assertEquals(6, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void valueCardinality_compactCol_Name() {
        DataFrame df = new ParquetLoader()
                .compactCol("a")
                .compactCol("b")
                .load(_TEST1_PARQUET);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }
}
