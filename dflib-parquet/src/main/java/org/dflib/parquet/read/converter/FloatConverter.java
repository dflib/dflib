package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.dflib.builder.FloatAccum;
import org.dflib.builder.FloatHolder;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class FloatConverter extends StoringPrimitiveConverter<Float> {

    public static FloatConverter of(boolean accum, int accumCapacity, boolean dictionarySupport, boolean allowsNulls) {
        ValueStore<Float> store = allowsNulls
                ? (accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>())
                : (accum ? new FloatAccum(accumCapacity) : new FloatHolder());

        return new FloatConverter(store, dictionarySupport, allowsNulls);
    }

    private Float[] dict;

    protected FloatConverter(ValueStore<Float> store, boolean dictionarySupport, boolean allowsNulls) {
        super(store, dictionarySupport, allowsNulls);
    }

    @Override
    public void addFloat(float value) {
        store.pushFloat(value);
    }

    @Override
    public boolean hasDictionarySupport() {
        // if we are boxing to non-primitives, we might as well use the dictionary
        return allowsNulls && dictionarySupport;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        this.dict = new Float[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = dictionary.decodeToFloat(i);
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }
}
