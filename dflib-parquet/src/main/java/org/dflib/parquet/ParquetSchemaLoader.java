package org.dflib.parquet;

import dev.hardwood.InputFile;
import dev.hardwood.reader.ParquetFileReader;
import dev.hardwood.schema.FileSchema;
import org.dflib.ByteSource;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

/**
 * Reads the schema of a .parquet file without reading its data.
 */
public class ParquetSchemaLoader {

    public FileSchema load(Path filePath) {
        return loadFromInputFile(InputFile.of(filePath), filePath.toString());
    }

    public FileSchema load(String filePath) {
        return load(new File(filePath));
    }

    public FileSchema load(File file) {
        return load(file.toPath());
    }

    /**
     * @since 1.1.0
     */
    public FileSchema load(ByteSource src) {
        return load(src.asBytes());
    }

    /**
     * @since 1.1.0
     */
    public FileSchema load(byte[] bytes) {
        return loadFromInputFile(InputFile.of(ByteBuffer.wrap(bytes)), "?");
    }

    private FileSchema loadFromInputFile(InputFile inputFile, String resourceId) {
        try (ParquetFileReader reader = ParquetFileReader.open(inputFile)) {
            return reader.getFileSchema();
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading Parquet schema: " + resourceId, e);
        }
    }
}
