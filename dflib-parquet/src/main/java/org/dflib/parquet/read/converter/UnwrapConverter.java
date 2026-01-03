package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.Type;

import java.util.function.Consumer;

class UnwrapConverter extends GroupConverter {

    private final StoringConverter childConverter;
    private final Consumer<Object> parentStore;

    public UnwrapConverter(Type unwrapType, Consumer<Object> parentStore) {
        // TODO: pass "dictionarySupport" in the constructor
        this.childConverter = StoringConverter.of(unwrapType, false, true);
        this.parentStore = parentStore;
    }

    @Override
    public void start() {
        // TODO: no support for primitives in the UnwrapConverter yet
        childConverter.store().push(null);
    }

    @Override
    public void end() {
        // TODO: no support for primitives in the UnwrapConverter yet
        parentStore.accept(childConverter.holder().get());
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return childConverter.converter();
    }
}
