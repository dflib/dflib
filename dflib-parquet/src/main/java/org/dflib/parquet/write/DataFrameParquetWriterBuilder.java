package org.dflib.parquet.write;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.conf.ParquetConfiguration;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.OutputFile;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.dflib.row.RowProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.0-M23
 */
public class DataFrameParquetWriterBuilder extends ParquetWriter.Builder<RowProxy, DataFrameParquetWriterBuilder> {

    private final Map<String, String> extraMetaData;
    private WriteConfiguration writeConfiguration;
    private DataFrameSchema schema;

    public DataFrameParquetWriterBuilder(OutputFile outputFile) {
        super(outputFile);
        this.extraMetaData = new HashMap<>();
    }

    @Override
    protected DataFrameParquetWriterBuilder self() {
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

    public DataFrameParquetWriterBuilder withWriteConfiguration(WriteConfiguration writeConfiguration) {
        this.writeConfiguration = writeConfiguration;
        return self();
    }

    public DataFrameParquetWriterBuilder withSchema(DataFrameSchema schema) {
        this.schema = schema;
        return self();
    }

    private WriteSupport<RowProxy> getWriteSupport() {
        return new DataFrameParquetWriterSupport(extraMetaData, writeConfiguration, schema);
    }

    private static class DataFrameParquetWriterSupport extends WriteSupport<RowProxy> {

        private final Map<String, String> extraMetaData;
        private final WriteConfiguration writeConfiguration;
        private final DataFrameSchema dataFrameSchema;
        private RowWriter rowWriter;

        DataFrameParquetWriterSupport(
                Map<String, String> extraMetaData,
                WriteConfiguration writeConfiguration,
                DataFrameSchema dataFrameSchema) {

            this.extraMetaData = extraMetaData;
            this.writeConfiguration = writeConfiguration;
            this.dataFrameSchema = dataFrameSchema;
        }

        @Override
        public String getName() {
            return "DFLib";
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
