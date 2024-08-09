package org.dflib.parquet;

import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

public class BaseParquetTest {

    @TempDir
    static Path OUT_BASE;

    protected static File tempFile(String name) {
        return tempPath(name).toFile();
    }

    protected static Path tempPath(String name) {
        return OUT_BASE.resolve(name);
    }
}
