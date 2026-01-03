package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.time.LocalTime;

class LocalTimeMillisConverter extends StoringPrimitiveConverter<LocalTime> {

    public static LocalTimeMillisConverter of(boolean accum, int accumCapacity, boolean dictionarySupport, boolean allowsNulls) {
        ValueStore<LocalTime> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new LocalTimeMillisConverter(store, dictionarySupport, allowsNulls);
    }

    private LocalTime[] dict;

    protected LocalTimeMillisConverter(ValueStore<LocalTime> store, boolean dictionarySupport, boolean allowsNulls) {
        super(store, dictionarySupport, allowsNulls);
    }

    @Override
    public void addInt(int millisInDay) {
        store.push(convert(millisInDay));
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        this.dict = new LocalTime[maxId + 1];

        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToInt(i));
        }
    }

    private LocalTime convert(int time) {
        return LocalTime.ofNanoOfDay(time * 1_000_000L);
    }

}
