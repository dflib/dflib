package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;

public class NoNullsRowConverter extends GroupConverter {

    private final StoringConverter[] converters;
    
    public NoNullsRowConverter(StoringConverter[] converters) {
        this.converters = converters;
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
    }
}
