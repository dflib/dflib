package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvroLoaderTest {

    static File _TEST1_AVRO;
    static File _TEST2_AVRO;

    @BeforeAll
    public static void setupCsvDirs() throws URISyntaxException {
        _TEST1_AVRO = new File(AvroLoaderTest.class.getResource("test1.avro").toURI()).getAbsoluteFile();
        _TEST2_AVRO = new File(AvroLoaderTest.class.getResource("test2.avro").toURI()).getAbsoluteFile();
    }

    @Test
    public void nulls() {
        DataFrame df = new AvroLoader().load(_TEST1_AVRO);

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

        assertEquals(6, idCardinality.getColumn(0).unique().size());
        assertEquals(6, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void noNulls() {
        DataFrame df = new AvroLoader().load(_TEST2_AVRO);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(4, idCardinality.getColumn(1).unique().size());
    }
}
