package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.Binary;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class StringConverter extends StoringPrimitiveConverter<String> {

    public static StringConverter of(boolean accum, int accumCapacity, boolean allowsNulls) {
        ValueStore<String> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new StringConverter(store, allowsNulls);
    }

    private String[] dict;

    protected StringConverter(ValueStore<String> store, boolean allowsNulls) {
        super(store, allowsNulls);
    }

    @Override
    public void addBinary(Binary value) {
        store.push(convert(value));
    }

    private String convert(Binary value) {
        return value.toStringUsingUTF8();
    }

    @Override
    public boolean hasDictionarySupport() {
        return true;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        dict = new String[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToBinary(i));
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }
}