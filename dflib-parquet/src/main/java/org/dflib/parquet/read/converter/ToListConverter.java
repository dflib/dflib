package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.schema.Type;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.util.ArrayList;
import java.util.List;

class ToListConverter extends StoringGroupConverter<List<Object>> {

    public static ToListConverter of(boolean accum, int accumCapacity, boolean allowsNulls, Type elementType) {
        ValueStore<List<Object>> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new ToListConverter(store, allowsNulls, elementType);
    }

    private final Converter elementConverter;

    private List<Object> elements;

    protected ToListConverter(ValueStore<List<Object>> store, boolean allowsNulls, Type elementType) {
        super(store, allowsNulls);
        this.elementConverter = new UnwrapConverter(elementType, v -> elements.add(v));
    }

    @Override
    public void start() {
        this.elements = new ArrayList<>();
    }

    @Override
    public void end() {
        store.push(elements);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return elementConverter;
    }
}
