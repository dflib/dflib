package org.dflib.parquet.read;

import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.InputFile;

public class DataFrameParquetReaderBuilder extends ParquetReader.Builder<Object[]> {

    public DataFrameParquetReaderBuilder(InputFile file) {
        super(file);
    }

    @Override
    protected ReadSupport<Object[]> getReadSupport() {
        return new DataFrameReadSupport();
    }

}