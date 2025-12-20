package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.Type;

import java.util.function.Consumer;

class UnwrapConverter extends GroupConverter {

    private final Converter childConverter;
    private final Consumer<Object> parentConsumer;
    private Object v;

    public UnwrapConverter(Type unwrapType, Consumer<Object> parentConsumer) {
        this.childConverter = Converters.converter(unwrapType, v -> this.v = v);
        this.parentConsumer = parentConsumer;
    }

    @Override
    public void start() {
        v = null;
    }

    @Override
    public void end() {
        parentConsumer.accept(v);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return childConverter;
    }
}
