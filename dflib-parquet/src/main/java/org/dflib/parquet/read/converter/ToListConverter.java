package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class ToListConverter extends GroupConverter {

    private final Consumer<Object> parentConsumer;
    private final Converter elementConverter;

    private List<Object> elements;

    public ToListConverter(Type elementType, Consumer<Object> parentConsumer) {
        this.parentConsumer = parentConsumer;
        this.elementConverter = new UnwrapConverter(elementType, v -> elements.add(v));
    }

    @Override
    public void start() {
        this.elements = new ArrayList<>();
    }

    @Override
    public void end() {
        parentConsumer.accept(elements);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return elementConverter;
    }
}
