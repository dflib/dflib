package org.dflib.parquet.write;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.conf.ParquetConfiguration;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.OutputFile;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.dflib.row.RowProxy;

public class DataframeParquetWriterBuilder extends ParquetWriter.Builder<RowProxy, DataframeParquetWriterBuilder> {

    private final Map<String, String> extraMetaData = new HashMap<>();
    private WriteConfiguration writeConfiguration;
    private DataframeSchema dataFrameSchema;

    public DataframeParquetWriterBuilder(OutputFile outputFile) {
        super(outputFile);
    }

    @Override
    protected DataframeParquetWriterBuilder self() {
        return this;
    }

    @Override
    protected WriteSupport<RowProxy> getWriteSupport(ParquetConfiguration conf) {
        return getWriteSupport();
    }

    @Override
    protected WriteSupport<RowProxy> getWriteSupport(Configuration conf) {
        return getWriteSupport();
    }

    public DataframeParquetWriterBuilder withWriteConfiguration(WriteConfiguration writeConfiguration) {
        this.writeConfiguration = writeConfiguration;
        return self();
    }

    public DataframeParquetWriterBuilder withDataFrameSchema(DataframeSchema dataFrameSchema) {
        this.dataFrameSchema = dataFrameSchema;
        return self();
    }

    protected WriteSupport<RowProxy> getWriteSupport() {
        return new DataframeParquetWriterSupport(extraMetaData, writeConfiguration, dataFrameSchema);
    }

    private static class DataframeParquetWriterSupport extends WriteSupport<RowProxy> {

        private final Map<String, String> extraMetaData;
        private final WriteConfiguration writeConfiguration;
        private final DataframeSchema dataFrameSchema;
        private RowWriter rowWriter;

        DataframeParquetWriterSupport(Map<String, String> extraMetaData, WriteConfiguration writeConfiguration,
                DataframeSchema dataFrameSchema) {
            this.extraMetaData = extraMetaData;
            this.writeConfiguration = writeConfiguration;
            this.dataFrameSchema = dataFrameSchema;
        }

        @Override
        public String getName() {
            return ParquetSchemaCompiler.DEFAULT_NAME;
        }

        @Override
        public WriteContext init(Configuration configuration) {
            ParquetSchemaCompiler schemaCompiler = new ParquetSchemaCompiler(writeConfiguration);
            MessageType schema = schemaCompiler.compileSchema(dataFrameSchema);
            return new WriteContext(schema, this.extraMetaData);
        }

        @Override
        public void prepareForWrite(RecordConsumer recordConsumer) {
            rowWriter = new RowWriter(recordConsumer, writeConfiguration, dataFrameSchema);
        }

        @Override
        public void write(RowProxy record) {
            rowWriter.write(record);
        }

    }

}
