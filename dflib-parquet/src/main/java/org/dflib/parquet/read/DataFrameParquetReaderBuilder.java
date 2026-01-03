package org.dflib.parquet.read;

import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.MessageType;

public class DataFrameParquetReaderBuilder extends ParquetReader.Builder<Object> {

    private final MessageType schema;
    private final GroupConverter rowConverter;

    public DataFrameParquetReaderBuilder(InputFile file, MessageType schema, GroupConverter rowConverter) {
        super(file);
        this.schema = schema;
        this.rowConverter = rowConverter;
    }

    @Override
    protected ReadSupport<Object> getReadSupport() {
        return new DataFrameReadSupport(schema, rowConverter);
    }
}