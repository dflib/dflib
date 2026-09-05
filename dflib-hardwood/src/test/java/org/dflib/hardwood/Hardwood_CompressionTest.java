package org.dflib.hardwood;

import org.dflib.DataFrame;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Hardwood_CompressionTest {

    @TempDir
    static Path uncompressedBase;

    @TempDir
    Path outBase;

    static final DataFrame df = DataFrame.foldByRow("a", "b", "c")
            .of(
                    // create data that can be reasonably compressed
                    4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd",
                    4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd",
                    4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    static long UNCOMPRESSED_SIZE;

    @BeforeAll
    static void measureUncompressed() {
        Path out = uncompressedBase.resolve("uncompressed.parquet");
        Hardwood.saver().save(df, out);

        UNCOMPRESSED_SIZE = out.toFile().length();
        assertTrue(UNCOMPRESSED_SIZE > 0);
    }

    private void checkCompressed(Path path) {
        assertTrue(path.toFile().length() < UNCOMPRESSED_SIZE, () -> "Failed to compress: " + path.toFile().length() + " vs " + UNCOMPRESSED_SIZE);
    }

    @Test
    public void gzip() {
        Path out = outBase.resolve("gzip.parquet");
        Hardwood.saver()
                .compression(CompressionCodec.GZIP)
                .save(df, out);

        checkCompressed(out);

        DataFrame loaded = Hardwood.loader().load(out);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(1, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(2, 4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void snappy() {
        Path out = outBase.resolve("snappy.parquet");
        Hardwood.saver()
                .compression(CompressionCodec.SNAPPY)
                .save(df, out);

        checkCompressed(out);

        DataFrame loaded = Hardwood.loader().load(out);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(1, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(2, 4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void lz4_raw() {
        Path out = outBase.resolve("lz4_raw.parquet");
        Hardwood.saver()
                .compression(CompressionCodec.LZ4_RAW)
                .save(df, out);

        checkCompressed(out);

        DataFrame loaded = Hardwood.loader().load(out);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(1, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(2, 4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void zstd() {
        Path out = outBase.resolve("zstd.parquet");
        Hardwood.saver()
                .compression(CompressionCodec.ZSTD)
                .save(df, out);

        checkCompressed(out);

        DataFrame loaded = Hardwood.loader().load(out);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(1, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(2, 4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }
}
