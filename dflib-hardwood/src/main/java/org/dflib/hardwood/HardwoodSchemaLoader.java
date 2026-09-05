package org.dflib.hardwood;

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
 *
 * <p>Unlike the "dflib-parquet" module, which returns an {@code org.apache.parquet.schema.MessageType}, this loader
 * returns Hardwood's own {@link FileSchema}. There is no common Parquet schema type shared by the two engines, so
 * this is the one place where the two modules can not present the same API.
 *
 * @since 2.0.0
 */
public class HardwoodSchemaLoader {

    public FileSchema load(Path filePath) {
        return loadFromInputFile(InputFile.of(filePath), filePath.toString());
    }

    public FileSchema load(String filePath) {
        return load(new File(filePath));
    }

    public FileSchema load(File file) {
        return load(file.toPath());
    }

    public FileSchema load(ByteSource src) {
        return load(src.asBytes());
    }

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
