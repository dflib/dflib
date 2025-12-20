package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.Type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

class ToMapConverter extends GroupConverter {

    private final Consumer<Object> parentConsumer;
    private final Converter kvConverter;

    private Map<Object, Object> elements;

    public ToMapConverter(Type keyType, Type valueType, Consumer<Object> parentConsumer) {
        this.parentConsumer = parentConsumer;

        // note that we can't use a method ref here (elements::put), as elements is null initially
        // and is reset on every run
        this.kvConverter = new UnwrapBiConverter(keyType, valueType, (k, v) -> elements.put(k, v));
    }

    @Override
    public void start() {
        this.elements = new LinkedHashMap<>();
    }

    @Override
    public void end() {
        parentConsumer.accept(elements);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return kvConverter;
    }
}
