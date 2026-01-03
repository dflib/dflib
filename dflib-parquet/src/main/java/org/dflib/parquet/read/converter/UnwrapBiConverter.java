package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.Type;

import java.util.function.BiConsumer;

class UnwrapBiConverter extends GroupConverter {

    private final StoringConverter childConverter1;
    private final StoringConverter childConverter2;
    private final BiConsumer<Object, Object> parentConsumer;

    public UnwrapBiConverter(Type unwrapType1, Type unwrapType2, BiConsumer<Object, Object> parentConsumer) {
        this.childConverter1 = StoringConverter.of(unwrapType1, false);
        this.childConverter2 = StoringConverter.of(unwrapType2, false);
        this.parentConsumer = parentConsumer;
    }

    @Override
    public void start() {
        childConverter1.store().push(null);
        childConverter2.store().push(null);
    }

    @Override
    public void end() {
        parentConsumer.accept(childConverter1.holder().get(), childConverter2.holder().get());
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return switch (fieldIndex) {
            case 0 -> childConverter1.converter();
            case 1 -> childConverter2.converter();
            default -> throw new ArrayIndexOutOfBoundsException(fieldIndex);
        };
    }
}
