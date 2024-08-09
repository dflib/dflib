package org.dflib.parquet;

import org.junit.jupiter.api.io.TempDir;

import java.io.File;

public class BaseParquetTest {

    @TempDir
    static File OUT_BASE;

    protected static String outPath(String name) {
        return OUT_BASE.getPath() + File.separator + name;
    }
}
