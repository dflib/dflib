package org.dflib.s3.formats;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.excel.Excel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

public class S3ExcelTest extends S3FormatTest {

    private static final String ROOT = "excel";
    private static final String SHEET = "Sheet1";

    @BeforeAll
    static void setUp() {
        createTestData((df, path) -> Excel.saveSheet(df, path, SHEET), sampleData, ROOT + "/data1.xlsx");
        createTestData((df, path) -> Excel.saveSheet(df, path, SHEET), sampleData, ROOT + "/data2.xlsx");
    }

    @Override
    protected BiFunction<String, ByteSource, DataFrame> processor() {
        return (key, source) -> Excel.loadSheet(source, SHEET);
    }

    @Test
    void readSingleFile() {
        DataFrame result = saveToPath(ROOT + "/data1.xlsx");

        assertNotNull(result);
        assertEquals(3, result.height()); // +1 for excel columns
        assertEquals(2, result.width());
        assertEquals("1", result.getColumn("A").get(1));
        assertEquals("3", result.getColumn("B").get(1));
    }

    @Test
    void readMultipleFiles() {
        Map<String, DataFrame> results = loadAllData(ROOT);
        assertEquals(2, results.size());

        DataFrame df1 = results.get(ROOT + "/data1.xlsx");
        assertNotNull(df1);
        assertEquals(2, df1.width());

        DataFrame df2 = results.get(ROOT + "/data2.xlsx");
        assertNotNull(df2);
        assertEquals(2, df2.width());
    }
}
