package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.PrimitiveConverter;
import org.dflib.builder.ValueStore;

/**
 * @since 2.0.0
 */
public abstract class StoringPrimitiveConverter<T> extends PrimitiveConverter implements StoringConverter {

    protected final ValueStore<T> store;
    protected final boolean dictionarySupport;
    protected final boolean allowsNulls;

    protected StoringPrimitiveConverter(ValueStore<T> store, boolean dictionarySupport, boolean allowsNulls) {
        this.store = store;
        this.dictionarySupport = dictionarySupport;
        this.allowsNulls = allowsNulls;
    }

    @Override
    public boolean allowsNulls() {
        return allowsNulls;
    }

    @Override
    public boolean hasDictionarySupport() {
        return dictionarySupport;
    }

    @Override
    public Converter converter() {
        return this;
    }

    @Override
    public ValueStore<?> store() {
        return store;
    }

}
