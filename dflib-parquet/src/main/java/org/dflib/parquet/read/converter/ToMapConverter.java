package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.schema.Type;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.util.LinkedHashMap;
import java.util.Map;

class ToMapConverter extends StoringGroupConverter<Map<Object, Object>> {

    public static ToMapConverter of(boolean accum, int accumCapacity, boolean allowsNulls, Type keyType, Type valueType) {
        ValueStore<Map<Object, Object>> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new ToMapConverter(store, allowsNulls, keyType, valueType);
    }

    private final Converter kvConverter;

    private Map<Object, Object> elements;

    protected ToMapConverter(ValueStore<Map<Object, Object>> store, boolean allowsNulls, Type keyType, Type valueType) {
        super(store, allowsNulls);

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
        store.push(elements);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return kvConverter;
    }
}
