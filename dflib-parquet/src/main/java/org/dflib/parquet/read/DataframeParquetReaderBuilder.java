package org.dflib.parquet.read;

import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.InputFile;

public class DataframeParquetReaderBuilder extends ParquetReader.Builder<Row> {

    public DataframeParquetReaderBuilder(InputFile file) {
        super(file);
    }

    @Override
    protected ReadSupport<Row> getReadSupport() {
        return new DataframeReadSupport();
    }

}