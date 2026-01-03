package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.dflib.builder.LongAccum;
import org.dflib.builder.LongHolder;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class LongConverter extends StoringPrimitiveConverter<Long> {

    public static LongConverter of(boolean accum, int accumCapacity, boolean dictionarySupport, boolean allowsNulls) {
        ValueStore<Long> store = allowsNulls
                ? (accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>())
                : (accum ? new LongAccum(accumCapacity) : new LongHolder());

        return new LongConverter(store, dictionarySupport, allowsNulls);
    }

    private Long[] dict;

    protected LongConverter(ValueStore<Long> store, boolean dictionarySupport, boolean allowsNulls) {
        super(store, dictionarySupport, allowsNulls);
    }

    @Override
    public void addLong(long value) {
        store.pushLong(value);
    }

    @Override
    public boolean hasDictionarySupport() {
        // if we are boxing to non-primitives, we might as well use the dictionary
        return allowsNulls && dictionarySupport;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        this.dict = new Long[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = dictionary.decodeToLong(i);
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }
}
