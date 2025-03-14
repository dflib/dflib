package org.dflib.s3.formats;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.avro.Avro;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

public class S3AvroTest extends S3FormatTest {

    private static final String ROOT = "avro";

    @BeforeAll
    static void setUp() {
        createTestData(Avro::save, sampleData, ROOT + "/data1.avro");
        createTestData(Avro::save, sampleData, ROOT + "/data2.avro");
    }

    @Override
    protected BiFunction<String, ByteSource, DataFrame> processor() {
        return (key, source) -> Avro.load(source);
    }

    @Test
    void readSingleFile() {
        DataFrame result = saveToPath(ROOT + "/data1.avro");

        assertNotNull(result);
        assertEquals(2, result.height());
        assertEquals(2, result.width());
        assertEquals("1", result.getColumn("col1").get(0));
        assertEquals("3", result.getColumn("col2").get(0));
    }

    @Test
    void readMultipleFiles() {
        Map<String, DataFrame> results = loadAllData(ROOT);
        assertEquals(2, results.size());

        DataFrame df1 = results.get(ROOT + "/data1.avro");
        assertNotNull(df1);
        assertEquals(2, df1.width());

        DataFrame df2 = results.get(ROOT + "/data2.avro");
        assertNotNull(df2);
        assertEquals(2, df2.width());
    }
}
