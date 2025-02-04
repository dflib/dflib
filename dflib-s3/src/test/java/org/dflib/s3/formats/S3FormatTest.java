package org.dflib.s3.formats;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.s3.S3;
import org.dflib.s3.S3LocalTest;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class S3FormatTest extends S3LocalTest {

    protected static DataFrame sampleData;

    @BeforeAll
    static void setUp() {
        sampleData = DataFrame.foldByRow("col1", "col2").of(
                "1", "3",
                "2", "4"
        );
    }

    protected abstract BiFunction<String, ByteSource, DataFrame> processor();

    static protected void createTestData(BiConsumer<DataFrame, Path> saver, DataFrame df, String key) {
        try {
            File tempFile = File.createTempFile("dflib_s3Test", String.valueOf(key.hashCode()));
            assert tempFile.delete();
            saver.accept(df, tempFile.toPath());
            putTestObject(key, tempFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected DataFrame saveToPath(String key) {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(key)
                .build();

        ByteSource source = connector.source();
        return processor().apply(key, source);
    }

    protected Map<String, DataFrame> loadAllData(String key) {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(key)
                .build();

        return connector.sources().process(processor());
    }
}
