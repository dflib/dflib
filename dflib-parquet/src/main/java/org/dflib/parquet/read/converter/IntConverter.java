package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.dflib.builder.IntAccum;
import org.dflib.builder.IntHolder;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class IntConverter extends StoringPrimitiveConverter<Integer> {

    // TODO: support dictionary when nulls are allowed?

    public static IntConverter of(boolean accum, int accumCapacity, boolean dictionarySupport, boolean allowsNulls) {
        ValueStore<Integer> store = allowsNulls
                ? (accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>())
                : (accum ? new IntAccum(accumCapacity) : new IntHolder());

        return new IntConverter(store, dictionarySupport, allowsNulls);
    }

    private Integer[] dict;

    protected IntConverter(ValueStore<Integer> store, boolean dictionarySupport, boolean allowsNulls) {
        super(store, dictionarySupport, allowsNulls);
    }

    @Override
    public void addInt(int value) {
        store.pushInt(value);
    }

    @Override
    public boolean hasDictionarySupport() {
        // if we are boxing to non-primitives, we might as well use the dictionary
        return allowsNulls && dictionarySupport;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        dict = new Integer[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = dictionary.decodeToInt(i);
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }
}
