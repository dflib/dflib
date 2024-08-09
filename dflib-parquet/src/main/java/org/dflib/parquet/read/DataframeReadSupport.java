package org.dflib.parquet.read;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.api.InitContext;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.schema.MessageType;
import org.dflib.parquet.read.converter.RowConverter;

class DataframeReadSupport extends ReadSupport<Object[]> {

    @Override
    public RecordMaterializer<Object[]> prepareForRead(Configuration configuration, Map<String, String> keyValueMetaData,
            MessageType fileSchema, ReadContext readContext) {
        return new DataframeMaterializer(readContext.getRequestedSchema());
    }

    @Override
    public ReadContext init(InitContext initContext) {
        MessageType projection = initContext.getFileSchema();
        Map<String, String> metadata = new LinkedHashMap<>();
        return new ReadContext(projection, metadata);
    }

    private static class DataframeMaterializer extends RecordMaterializer<Object[]> {

        private final GroupConverter root;
        private Object[] value;

        public DataframeMaterializer(MessageType requestedSchema) {
            this.root = new RowConverter(requestedSchema, v -> this.value = v);
        }

        @Override
        public Object[] getCurrentRecord() {
            return value;
        }

        @Override
        public GroupConverter getRootConverter() {
            return root;
        }

    }
}
