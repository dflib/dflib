package org.dflib.parquet.meta;

import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.conf.PlainParquetConfiguration;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @since 2.0.0
 */
public class MetaReader {

    public static MessageType schema(InputFile inputFile, Supplier<String> resourceId) {
        ParquetReadOptions parquetReadOptions = ParquetReadOptions.builder(new PlainParquetConfiguration()).build();
        try (ParquetFileReader parquetFile = new ParquetFileReader(inputFile, parquetReadOptions)) {
            return parquetFile.getFileMetaData().getSchema();
        } catch (IOException e) {
            throw new RuntimeException("Error reading Parquet schema file: " + resourceId.get(), e);
        }
    }

    public static SchemaAndSize schemaAndSize(InputFile inputFile, Supplier<String> resourceId) {
        ParquetReadOptions parquetReadOptions = ParquetReadOptions.builder(new PlainParquetConfiguration()).build();
        try (ParquetFileReader parquetFile = new ParquetFileReader(inputFile, parquetReadOptions)) {

            long size = parquetFile.getFooter().getBlocks().stream().mapToLong(b -> b.getRowCount()).sum();
            if (size > (long) Integer.MAX_VALUE) {
                throw new IllegalStateException("Parquet file is too large. Can read a max of " + Integer.MAX_VALUE + " rows, actual size: " + size);
            }

            return new SchemaAndSize(parquetFile.getFileMetaData().getSchema(), (int) size);

        } catch (IOException e) {
            throw new RuntimeException("Error reading Parquet schema file: " + resourceId.get(), e);
        }
    }


    public record SchemaAndSize(MessageType schema, int size) {
    }
}
