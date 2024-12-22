package org.dflib.parquet;

import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.conf.PlainParquetConfiguration;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.schema.MessageType;
import org.dflib.connector.ByteSource;
import org.dflib.parquet.read.BytesInputFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;


public class ParquetSchemaLoader {

    public MessageType load(Path filePath) {
        return loadFromInputFile(new LocalInputFile(filePath), filePath::toString);
    }

    public MessageType load(String filePath) {
        return load(new File(filePath));
    }

    public MessageType load(File file) {
        return load(file.toPath());
    }

    /**
     * @since 1.1.0
     */
    public MessageType load(ByteSource src) {
        return load(src.asBytes());
    }

    /**
     * @since 1.1.0
     */
    public MessageType load(byte[] bytes) {
        return loadFromInputFile(new BytesInputFile(bytes), () -> "?");
    }

    private MessageType loadFromInputFile(InputFile inputFile, Supplier<String> resourceId) {

        ParquetReadOptions parquetReadOptions = ParquetReadOptions.builder(new PlainParquetConfiguration()).build();
        try (ParquetFileReader parquetFile = new ParquetFileReader(inputFile, parquetReadOptions)) {
            return parquetFile.getFileMetaData().getSchema();
        } catch (IOException e) {
            throw new RuntimeException("Error reading Parquet schema file: " + resourceId.get(), e);
        }
    }
}
