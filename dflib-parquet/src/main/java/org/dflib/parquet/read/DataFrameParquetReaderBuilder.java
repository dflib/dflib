package org.dflib.parquet.read;

import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.schema.MessageType;



public class DataFrameParquetReaderBuilder extends ParquetReader.Builder<Object[]> {

    private final MessageType projection;

    public DataFrameParquetReaderBuilder(InputFile file, MessageType projection) {
        super(file);
        this.projection = projection;
    }

    @Override
    protected ReadSupport<Object[]> getReadSupport() {
        return new DataFrameReadSupport(projection);
    }

}