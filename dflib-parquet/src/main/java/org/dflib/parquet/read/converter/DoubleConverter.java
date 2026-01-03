package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.dflib.builder.DoubleAccum;
import org.dflib.builder.DoubleHolder;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class DoubleConverter extends StoringPrimitiveConverter<Double> {

    public static DoubleConverter of(boolean accum, int accumCapacity, boolean dictionarySupport, boolean allowsNulls) {
        ValueStore<Double> store = allowsNulls
                ? (accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>())
                : (accum ? new DoubleAccum(accumCapacity) : new DoubleHolder());

        return new DoubleConverter(store, dictionarySupport, allowsNulls);
    }

    private Double[] dict;

    protected DoubleConverter(ValueStore<Double> store, boolean dictionarySupport, boolean allowsNulls) {
        super(store, dictionarySupport, allowsNulls);
    }

    @Override
    public void addDouble(double value) {
        store.pushDouble(value);
    }

    @Override
    public boolean hasDictionarySupport() {
        // if we are boxing to non-primitives, we might as well use the dictionary
        return allowsNulls && dictionarySupport;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        this.dict = new Double[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = dictionary.decodeToDouble(i);
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }
}
