package org.dflib.parquet.read;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.api.InitContext;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.schema.MessageType;

import java.util.Map;

class DataFrameReadSupport extends ReadSupport<Object> {

    private final MessageType schema;
    private final GroupConverter rowConverter;

    public DataFrameReadSupport(MessageType schema, GroupConverter rowConverter) {
        this.schema = schema;
        this.rowConverter = rowConverter;
    }

    @Override
    public ReadContext init(InitContext initContext) {
        return new ReadContext(schema, Map.of());
    }

    @Override
    public RecordMaterializer<Object> prepareForRead(
            Configuration configuration,
            Map<String, String> keyValueMetaData,
            MessageType fileSchema,
            ReadContext readContext) {

        Object resultPlaceholder = new Object();

        return new RecordMaterializer<>() {
            @Override
            public Object getCurrentRecord() {
                return resultPlaceholder;
            }

            @Override
            public GroupConverter getRootConverter() {
                return rowConverter;
            }
        };
    }
}
