package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.Type;

import java.util.function.BiConsumer;

class UnwrapBiConverter extends GroupConverter {

    private final Converter childConverter1;
    private final Converter childConverter2;
    private final BiConsumer<Object, Object> parentConsumer;

    private Object v1;
    private Object v2;

    public UnwrapBiConverter(Type unwrapType1, Type unwrapType2, BiConsumer<Object, Object> parentConsumer) {
        this.childConverter1 = Converters.converter(unwrapType1, v -> this.v1 = v);
        this.childConverter2 = Converters.converter(unwrapType2, v -> this.v2 = v);
        this.parentConsumer = parentConsumer;
    }

    @Override
    public void start() {
        v1 = null;
        v2 = null;
    }

    @Override
    public void end() {
        parentConsumer.accept(v1, v2);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return switch (fieldIndex) {
            case 0 -> childConverter1;
            case 1 -> childConverter2;
            default -> throw new ArrayIndexOutOfBoundsException(fieldIndex);
        };
    }
}
