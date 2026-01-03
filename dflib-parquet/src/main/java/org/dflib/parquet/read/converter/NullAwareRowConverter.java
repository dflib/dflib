package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.dflib.builder.ValueAccum;

import java.util.stream.Stream;

public class NullAwareRowConverter extends GroupConverter {

    private final StoringConverter[] converters;
    private final StoringConverter[] nullableConverters;

    private int read;

    public NullAwareRowConverter(StoringConverter[] converters, StoringConverter[] nullableConverters) {
        this.converters = converters;
        this.nullableConverters = nullableConverters;
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return converters[fieldIndex].converter();
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
        // push nulls if needed
        int read = ++this.read;
        int len = nullableConverters.length;
        for (int i = 0; i < len; i++) {
            ValueAccum<?> accum = nullableConverters[i].accum();
            if (accum.size() < read) {
                accum.push(null);
            }
        }
    }
}
