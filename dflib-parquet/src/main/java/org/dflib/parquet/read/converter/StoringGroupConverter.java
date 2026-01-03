package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.dflib.builder.ValueStore;

/**
 * @since 2.0.0
 */
public abstract class StoringGroupConverter<T> extends GroupConverter implements StoringConverter {

    protected final ValueStore<T> store;
    protected final boolean allowsNulls;

    protected StoringGroupConverter(ValueStore<T> store, boolean allowsNulls) {
        this.store = store;
        this.allowsNulls = allowsNulls;
    }

    @Override
    public boolean allowsNulls() {
        return allowsNulls;
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
