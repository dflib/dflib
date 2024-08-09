package org.dflib.parquet;

import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.conf.PlainParquetConfiguration;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.schema.MessageType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @since 1.0.0-M23
 */
public class ParquetSchemaLoader {

    public MessageType load(Path filePath) {
        LocalInputFile inputFile = new LocalInputFile(filePath);

        ParquetReadOptions parquetReadOptions = ParquetReadOptions.builder(new PlainParquetConfiguration()).build();
        try (ParquetFileReader parquetFile = new ParquetFileReader(inputFile, parquetReadOptions)) {
            return parquetFile.getFileMetaData().getSchema();
        } catch (IOException e) {
            throw new RuntimeException("Error reading Parquet schema file: " + filePath, e);
        }
    }

    public MessageType load(String filePath) {
        return load(new File(filePath));
    }

    public MessageType load(File file) {
        return load(file.toPath());
    }
}
